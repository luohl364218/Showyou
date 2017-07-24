package com.zju.campustour.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.zju.campustour.R;
import com.zju.campustour.presenter.protocal.enumerate.IdentityType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IdentityConfirmActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.id_college_student)
    Button collegeStudentBtn;
    @BindView(R.id.id_school_student)
    Button schoolStudentBtn;
    @BindView(R.id.id_look_around)
    Button lookAroundBtn;
    @BindView(R.id.id_graduate_student)
    Button graduateStudentBtn;
    @BindView(R.id.id_school_teacher)
    Button schoolTeacherBtn;
    @BindView(R.id.id_student_parent)
    Button studentParentBtn;
    @BindView(R.id.btn_next)
    Button nextConfirm;

    boolean isCollegeStudent = false;
    boolean isSchoolStudent = false;
    boolean isLookAround = false;
    boolean isGraduateStudent = false;
    boolean isSchoolTeacher = false;
    boolean isStudentParent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_confirm);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

        collegeStudentBtn.setOnClickListener(this);
        schoolStudentBtn.setOnClickListener(this);
        lookAroundBtn.setOnClickListener(this);
        graduateStudentBtn.setOnClickListener(this);
        schoolTeacherBtn.setOnClickListener(this);
        studentParentBtn.setOnClickListener(this);

        nextConfirm.setEnabled(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.id_college_student:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                collegeStudentBtn.setSelected(true);
                isCollegeStudent = true;
                break;
            case R.id.id_school_student:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                schoolStudentBtn.setSelected(true);
                isSchoolStudent = true;
                break;
            case R.id.id_look_around:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                lookAroundBtn.setSelected(true);
                isLookAround = true;
                break;
            case R.id.id_school_teacher:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                schoolTeacherBtn.setSelected(true);
                isSchoolTeacher = true;
                break;
            case R.id.id_graduate_student:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                graduateStudentBtn.setSelected(true);
                isGraduateStudent = true;
                break;
            case R.id.id_student_parent:
                nextConfirm.setEnabled(true);
                nextConfirm.setSelected(true);
                clearBtnSelectState();
                studentParentBtn.setSelected(true);
                isStudentParent = true;
                break;
        }
    }

    public void clearBtnSelectState(){
        collegeStudentBtn.setSelected(false);
        schoolStudentBtn.setSelected(false);
        lookAroundBtn.setSelected(false);
        graduateStudentBtn.setSelected(false);
        schoolTeacherBtn.setSelected(false);
        studentParentBtn.setSelected(false);

        isCollegeStudent = false;
        isSchoolStudent = false;
        isLookAround = false;
        isGraduateStudent = false;
        isSchoolTeacher = false;
        isStudentParent = false;
    }

    @OnClick(R.id.btn_next)
    public void onNextBtnClicked(){
        if (isCollegeStudent){
            startActivity(new Intent(this,RegisterCollegeStudentActivity.class));
        }
        else if (isSchoolStudent){
            startActivity(new Intent(this,RegisterSchoolStudentActivity.class));
        }
        else if (isLookAround){
            startActivity(new Intent(this, RegisterLookAroundActivity.class));
        }
        else if (isGraduateStudent){
            startActivity(new Intent(this,RegisterGraduateStudentActivity.class));
        }
        else if (isSchoolTeacher){
            startActivity(new Intent(this,RegisterTeacherActivity.class));
        }
        else if (isStudentParent){
            startActivity(new Intent(this,RegisterParentActivity.class));
        }
        else {
            showToast("出错啦");
        }
    }
}
