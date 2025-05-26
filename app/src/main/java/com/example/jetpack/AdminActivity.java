package com.example.jetpack;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    private DBHelper DB;
    private ListView userListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        DB = new DBHelper(this);
        userListView = findViewById(R.id.userListView);

        // 初始化适配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        userListView.setAdapter(adapter);

        refreshUserList();

        // 设置长按删除功能
        userListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String username = userList.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("删除用户")
                    .setMessage("确定要删除用户 " + username + " 吗?")
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (DB.deleteUser(username)) {
                            Toast.makeText(this, "用户删除成功", Toast.LENGTH_SHORT).show();
                            refreshUserList();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        });

        // 设置点击修改功能
        userListView.setOnItemClickListener((parent, view, position, id) -> {
            String oldUsername = userList.get(position);
            showEditUserDialog(oldUsername);
        });
    }

    private void refreshUserList() {
        userList.clear();
        Cursor cursor = DB.getAllUsers();
        while (cursor.moveToNext()) {
            userList.add(cursor.getString(0)); // 添加用户名
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showEditUserDialog(String oldUsername) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etPassword = view.findViewById(R.id.etPassword);

        new AlertDialog.Builder(this)
                .setTitle("编辑用户")
                .setView(view)
                .setPositiveButton("保存", (dialog, which) -> {
                    String newUsername = etUsername.getText().toString();
                    String newPassword = etPassword.getText().toString();

                    if (DB.updateUser(oldUsername, newUsername, newPassword)) {
                        Toast.makeText(this, "用户信息已更新", Toast.LENGTH_SHORT).show();
                        refreshUserList();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}