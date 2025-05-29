package com.example.jetpack;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    private DBHelper DB;
    private ListView userListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userList = new ArrayList<>();
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        DB = new DBHelper(this);
        userListView = findViewById(R.id.userListView);
        searchEditText = findViewById(R.id.searchEditText);

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

        // 监听搜索输入框的回车键事件
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, android.view.KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = searchEditText.getText().toString().trim();
                    if (keyword.isEmpty()) {
                        refreshUserList();
                    } else {
                        searchUsers(keyword);
                    }
                    return true;
                }
                return false;
            }
        });

        // 为添加用户按钮设置点击事件监听器
        Button addUserButton = findViewById(R.id.button4);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUserDialog();
            }
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

    private void searchUsers(String keyword) {
        userList.clear();
        Cursor cursor = DB.searchUsers(keyword);
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

        // 将原本的用户名填充到 EditText 中
        etUsername.setText(oldUsername);

        new AlertDialog.Builder(this)
                .setTitle("编辑用户")
                .setView(view)
                .setPositiveButton("保存", (dialog, which) -> {
                    String newUsername = etUsername.getText().toString();
                    String newPassword = etPassword.getText().toString();

                    if (newUsername.length() > 20 || newPassword.length() > 20) {
                        Toast.makeText(this, "用户名和密码不能超过20个字符", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (DB.updateUser(oldUsername, newUsername, newPassword)) {
                        Toast.makeText(this, "用户信息已更新", Toast.LENGTH_SHORT).show();
                        refreshUserList();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showAddUserDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etPassword = view.findViewById(R.id.etPassword);

        new AlertDialog.Builder(this)
                .setTitle("添加用户")
                .setView(view)
                .setPositiveButton("保存", (dialog, which) -> {
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();

                    if (username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "请填写用户名和密码", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (username.length() > 20 || password.length() > 20) {
                        Toast.makeText(this, "用户名和密码不能超过20个字符", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (DB.insertData(username, password)) {
                        Toast.makeText(this, "用户添加成功", Toast.LENGTH_SHORT).show();
                        refreshUserList();
                    } else {
                        Toast.makeText(this, "用户添加失败，可能用户名已存在", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}