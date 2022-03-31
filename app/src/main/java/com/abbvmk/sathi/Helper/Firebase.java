package com.abbvmk.sathi.Helper;

import androidx.annotation.NonNull;

import com.abbvmk.sathi.BuildConfig;
import com.abbvmk.sathi.Fragments.Notice.Notice;
import com.abbvmk.sathi.Fragments.Posts.Post;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.screens.Admin.Designation.PendingDesignationClass;
import com.abbvmk.sathi.screens.PostViewer.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Firebase {


    public interface IntegerCallback {
        void result(int count);
    }

    public interface BooleanCallback {
        void result(boolean success);
    }

    public interface UserCallback {
        void result(User user);
    }

    public interface UsersCallback {
        void result(ArrayList<User> users);
    }

    public interface PostCallback {
        void result(Post post);
    }

    public interface PostsCallback {
        void result(ArrayList<Post> posts);
    }

    public interface CommentsCallback {
        void result(ArrayList<Comment> comments);
    }

    public interface NoticesCallback {
        void result(ArrayList<Notice> notices);
    }

    public interface StringsCallback {
        void result(ArrayList<String> strings);
    }

    public interface PendingDesignationCallback {
        void result(ArrayList<PendingDesignationClass> pendingDesignation);
    }

    public interface UpdateCallback {
        void result(File file);
    }

    //    ----------------------------------------------------------- USERS -------------------------------------------

    public static void fetchMyProfile(BooleanCallback callback) {
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currUser == null) {
            if (callback != null) {
                callback.result(false);
            }
            return;
        }
        fetchProfile(currUser.getUid(), user -> {
            if (user != null) {
                AuthHelper.saveUser(user);
            }
            if (callback != null) {
                callback.result(user != null);
            }
        });
    }

    public static void saveProfile(User user, @NonNull BooleanCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currUser == null) {
            callback.result(false);
            return;
        }
        user.setId(currUser.getUid());


        if (user.getMemberId() == 0) {
            generateMemberID(count -> {
                if (count == -1) {
                    callback.result(false);
                } else {
                    user.setMemberId(count);
                    db
                            .collection("users")
                            .document(currUser.getUid())
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                AuthHelper.saveUser(user);
                                callback.result(true);
                            })
                            .addOnFailureListener(e -> callback.result(false));
                }
            });
        } else {
            db
                    .collection("users")
                    .document(currUser.getUid())
                    .set(user)
                    .addOnSuccessListener(unused -> {
                        AuthHelper.saveUser(user);
                        callback.result(true);
                    })
                    .addOnFailureListener(e -> callback.result(false));
        }


    }

    private static void generateMemberID(@NonNull IntegerCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.getDocuments().size();
                    callback.result(count + 1);
                })
                .addOnFailureListener(e -> callback.result(-1));
    }


    public static void fetchProfile(String uid, UserCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (callback != null) {
                        callback.result(user);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.result(null);
                    }
                });
    }

    public static void fetchUsers(@NonNull UsersCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<User> list = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        User user = snapshot.toObject(User.class);
                        if (user != null) {
                            list.add(user);
                        }
                    }
                    callback.result(list);

                });
    }

//    ----------------------------------------------------------- POST -------------------------------------------

    public static void createPost(Post post, @NonNull BooleanCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        post.setUser(AuthHelper.getUID());

        DocumentReference postRef = db
                .collection("posts")
                .document();
        post.setId(postRef.getId());
        postRef
                .set(post)
                .addOnCompleteListener(task -> callback.result(task.isSuccessful()));

    }

    public static void fetchPosts(@NonNull PostsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("posts")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Post> list = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Post post = snapshot.toObject(Post.class);
                        if (post != null) {
                            list.add(post);
                        }
                    }
                    callback.result(list);

                })
                .addOnFailureListener(e -> callback.result(null));
    }

    public static void fetchPost(String postID, @NonNull PostCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("posts")
                .document(postID)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Post post = snapshot.toObject(Post.class);
                    callback.result(post);
                })
                .addOnFailureListener(e -> callback.result(null));
    }

    public static void deletePost(String postID, @NonNull BooleanCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("posts")
                .document(postID)
                .delete()
                .addOnCompleteListener(task -> callback.result(task.isSuccessful()));
    }

    public static void createComment(String postID, Comment comment, @NonNull BooleanCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        comment.setUser(AuthHelper.getUID());
        db
                .collection("posts")
                .document(postID)
                .collection("comments")
                .add(comment)
                .addOnCompleteListener(task -> callback.result(task.isSuccessful()));

    }

    public static void fetchComments(String postID, @NonNull CommentsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("posts")
                .document(postID)
                .collection("comments")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Comment> list = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Comment comment = snapshot.toObject(Comment.class);
                        if (comment != null) {
                            list.add(comment);
                        }
                    }
                    callback.result(list);

                })
                .addOnFailureListener(e -> callback.result(null));
    }


//    ----------------------------------------------------------- NOTICE -------------------------------------------

    public static void createNotice(Notice notice, @NonNull BooleanCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        notice.setUser(AuthHelper.getUID());

        DocumentReference noticeRef = db
                .collection("notices")
                .document();
        notice.setId(noticeRef.getId());
        noticeRef
                .set(notice)
                .addOnCompleteListener(task -> callback.result(task.isSuccessful()));

    }

    public static void fetchNotice(@NonNull NoticesCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("notices")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Notice> list = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Notice notice = snapshot.toObject(Notice.class);
                        if (notice != null) {
                            list.add(notice);
                        }
                    }
                    callback.result(list);

                })
                .addOnFailureListener(e -> callback.result(null));
    }

    public static void deleteNotice(String noticeID, @NonNull BooleanCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("notices")
                .document(noticeID)
                .delete()
                .addOnCompleteListener(task -> callback.result(task.isSuccessful()));
    }

    //    ----------------------------------------------------------- NOTICE -------------------------------------------


    public static void fetchDesignations(@NonNull StringsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("designations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> list = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        list.add(doc.get("title", String.class));
                    }
                    callback.result(list);
                })
                .addOnFailureListener(e -> callback.result(null));

    }

    public static void createDesignations(String designation, BooleanCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("title", designation);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("designations")
                .add(map)
                .addOnCompleteListener(task -> {
                    if (callback != null) {
                        callback.result(task.isSuccessful());
                    }
                });
    }

    public static void requestDesignationUpdate(PendingDesignationClass pending_designation, @NonNull BooleanCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db
                .collection("pending_designation")
                .document();

        pending_designation.setRequestedBy(AuthHelper.getUID());
        pending_designation.setId(ref.getId());

        ref
                .set(pending_designation)
                .addOnCompleteListener(task -> callback.result(task.isSuccessful()));
    }

    public static void fetchPendingDesignationUpdate(@NonNull PendingDesignationCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db
                .collection("pending_designation")
                .whereNotEqualTo("requestedBy", AuthHelper.getUID())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<PendingDesignationClass> list = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        PendingDesignationClass pending = snapshot.toObject(PendingDesignationClass.class);
                        if (pending != null) {
                            list.add(pending);
                        }
                    }
                    callback.result(list);

                })
                .addOnFailureListener(e -> callback.result(null));
    }

    public static void approvePendingRequest(String id, @NonNull BooleanCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference pendingDesignationRef = db
                .collection("pending_designation")
                .document(id);

        CollectionReference users = db
                .collection("users");

        pendingDesignationRef.get()
                .addOnFailureListener(e -> callback.result(false))
                .addOnSuccessListener(documentSnapshot -> {
                    PendingDesignationClass pending_designation = documentSnapshot.toObject(PendingDesignationClass.class);
                    if (pending_designation == null) {
                        callback.result(false);
                        return;
                    }


                    users
                            .whereEqualTo("designation", pending_designation.getDesignation())
                            .get()
                            .addOnFailureListener(e -> callback.result(false))
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    users
                                            .document(queryDocumentSnapshots.getDocuments().get(0).getId())
                                            .update("designation", "सदस्य")
                                            .addOnFailureListener(e -> callback.result(false))
                                            .addOnSuccessListener(unused -> {
                                                users
                                                        .document(pending_designation.getRequestedFor())
                                                        .update("designation", pending_designation.getDesignation())
                                                        .addOnCompleteListener(task -> {
                                                            callback.result(task.isSuccessful());
                                                            pendingDesignationRef.delete();
                                                        });
                                            });
                                } else {
                                    users
                                            .document(pending_designation.getRequestedFor())
                                            .update("designation", pending_designation.getDesignation())
                                            .addOnCompleteListener(task -> {
                                                callback.result(task.isSuccessful());
                                                pendingDesignationRef.delete();
                                            });
                                }
                            });

                });
    }

    public static void rejectPendingRequest(String id, @NonNull BooleanCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("pending_designation")
                .document(id)
                .delete()
                .addOnCompleteListener(task -> callback.result(task.isSuccessful()));
    }

    //    ----------------------------------------------------------- UPDATE -------------------------------------------


    public static void checkForUpdates(@NonNull UpdateCallback callback) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("app_updates")
                .whereGreaterThan("version", BuildConfig.VERSION_CODE)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                        return;
                    }
                    DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                    if (snapshot == null || !snapshot.contains("path")) {
                        return;
                    }

                    downloadUpdate(snapshot.get("path", String.class), callback);
                });
    }

    private static void downloadUpdate(String filename, @NonNull UpdateCallback callback) {
        File tempFile;
        try {
            tempFile = File.createTempFile("Update", ".apk");
        } catch (IOException e) {
            return;
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference noticeRef = storageRef.child("app_updates/android/" + filename);

        File finalTempFile = tempFile;
        noticeRef.getFile(finalTempFile)
                .addOnSuccessListener(taskSnapshot -> callback.result(tempFile));
    }

}
