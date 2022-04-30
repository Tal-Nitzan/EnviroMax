//package com.example.enviromax;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//
//import android.os.PersistableBundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link Fragment_PersonalDetails#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class Fragment_PersonalDetails extends Fragment {
//
//    private TextView personal_LBL_name;
//    private TextView personal_LBL_email;
//
//    public Fragment_PersonalDetails() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        findViews();
//        initViews();
//    }
//
//    private void findViews() {
//        personal_LBL_name = (TextView) getView().findViewById(R.id.personal_LBL_name);
//        personal_LBL_email = (TextView) getView().findViewById(R.id.personal_LBL_email);
//    }
//
//    private void initViews() {
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        personal_LBL_name.setText(user.getDisplayName());
//        personal_LBL_email.setText(user.getEmail());
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_personal_details, container, false);
//    }
//}