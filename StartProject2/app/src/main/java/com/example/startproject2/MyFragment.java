package com.example.startproject2;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    EditText name;
    EditText birth;
    EditText email;
    EditText pwd;

    RadioGroup gender;
    RadioButton male, female;

    CircleImageView circleImageView;
    File file;
    File file2;

    Bitmap picture;
    FrameLayout frameLayout;

    PaintBoard paintBoard;

    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {    //기생충
        super.onResume();
        file = createFile();
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), null);
            circleImageView.setImageBitmap(bitmap);
        }

        file2 = createFile2();
        if (file2.exists()){
            Log.d("file2","호출됩니다.");
            Bitmap bitmap2 = BitmapFactory.decodeFile(file2.getAbsolutePath(), null);
            if(bitmap2 == null){
                Log.d("file2","null입니다.");
            }else{
                Log.d("file2","not null입니다.");
            }
            //paintBoard = new PaintBoard(getContext());
            paintBoard.changeBitmap(bitmap2);
            //frameLayout = getContext().findViewById(R.id.signatureLayout);
            //frameLayout.addView(paintBoard);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my, container, false);

        name = rootView.findViewById(R.id.editText);
        birth = rootView.findViewById(R.id.editText2);
        email = rootView.findViewById(R.id.editText3);
        pwd = rootView.findViewById(R.id.editText4);

        gender = rootView.findViewById(R.id.radioGroup);
        male = rootView.findViewById(R.id.radioButton);
        female = rootView.findViewById(R.id.radioButton2);

        circleImageView = rootView.findViewById(R.id.imageView4);
        frameLayout = rootView.findViewById(R.id.signatureLayout);

        birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),listener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE));
                datePickerDialog.show();
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("sFile", Context.MODE_PRIVATE);

        String str_name = sharedPreferences.getString("name","");
        String str_birth = sharedPreferences.getString("birth","");
        String str_email = sharedPreferences.getString("email","");
        String str_pwd = sharedPreferences.getString("pwd","");

        int radioValue = sharedPreferences.getInt("gender",0);

        if(radioValue == 0){
            male.setChecked(false);
            female.setChecked(false);
        }
        else if(radioValue == R.id.radioButton){
            male.setChecked(true);
        }else if(radioValue == R.id.radioButton2){
            female.setChecked(true);
        }

        name.setText(str_name);
        birth.setText(str_birth);
        email.setText(str_email);
        pwd.setText(str_pwd);

        paintBoard = new PaintBoard(getContext());
        frameLayout.addView(paintBoard);

        Button button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintBoard.eraseCanvas();
            }
        });

        return rootView;
    }

    private void takePicture(){
        if(file == null){
            file = createFile();
        }
        Uri fileUri = FileProvider.getUriForFile(getActivity(), "com.example.startproject2.fileprovider",file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent, 101);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == getActivity().RESULT_OK){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            picture = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            circleImageView.setImageBitmap(picture);
        }
    }

    private File createFile() {
        String filename = "capture.jpg";
        File storageDir = getActivity().getExternalFilesDir(null);
        File outFile = new File(storageDir, filename);
        return outFile;
    }

    private File createFile2() {
        String filename = "signature.png";
        File storageDir = getActivity().getExternalFilesDir(null);
        File outFile = new File(storageDir, filename);
        return outFile;
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            birth.setText(i + "년 " + (i1+1) + "월 " + i2 + "일");
            //Toast.makeText(getContext(),i + "년 " + i1 + "월 " + i2 + "일",Toast.LENGTH_LONG).show();
        }
    };

    private String BitampConvertString(Bitmap bitmap){
        if(bitmap != null){
            try{
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                byte[] b = outputStream.toByteArray();
                String temp = Base64.encodeToString(b, Base64.DEFAULT);
                return temp;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("sFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); //사용자가 입력한 데이터 저장
        //sharedPreferences.edit().clear().commit(); //초기화

        String str_name = name.getText().toString();
        String str_birth = birth.getText().toString();
        String str_email = email.getText().toString();
        String str_pwd = pwd.getText().toString();
        String image = BitampConvertString(picture);
        //String signature = BitampToString(paintBoard.mBitmap);

        int radioGroup = gender.getCheckedRadioButtonId();

        editor.putString("name", str_name);
        editor.putString("birth", str_birth);
        editor.putString("email", str_email);
        editor.putString("pwd", str_pwd);
        editor.putInt("gender",radioGroup);
        editor.putString("image",image);
        //editor.putString("signature",signature);

        File signatureFile = new File(getActivity().getExternalFilesDir(null),"signature.png");
        Bitmap bitmap = paintBoard.mBitmap;
        Log.d("SavePaint","bitmap = " + bitmap);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        try {
            FileOutputStream fos = new FileOutputStream(signatureFile);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor.commit();
    }
}
