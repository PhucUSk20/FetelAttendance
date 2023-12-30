    package com.example.app8.UIAdmin;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.ActionBarDrawerToggle;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.Toolbar;
    import androidx.core.view.GravityCompat;
    import androidx.drawerlayout.widget.DrawerLayout;

    import android.content.Context;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.widget.ExpandableListView;
    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.Button;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.dvinfosys.model.HeaderModel;
    import com.dvinfosys.ui.NavigationListView;
    import com.example.app8.Login.AdminLogin;
    import com.example.app8.NavigationDrawer.Common;
    import com.example.app8.R;
    import com.example.app8.SQLServer.SQLConnection;
    import com.example.app8.NavigationDrawer.SettingsActivity;
    import com.example.app8.UICommon.ClassAddActivityForAdmin_Teacher;
    import com.example.app8.UICommon.CustomClassListAdapterForCommon;
    import com.google.android.material.navigation.NavigationView;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.ArrayList;

    public class UI_Admin extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener{
        private DrawerLayout drawer;
        private ActionBarDrawerToggle toggle;
        private ListView listView;
        private NavigationListView expandable_navigation;
        private Context context;
        private ArrayList<String> classList;
        private ArrayList<Integer> backgroundList;
        private ArrayList<Integer> studentCountList;
        private CustomClassListAdapterForCommon adapter;
        private Connection connection;
        private static final int ADD_ACCOUNT_REQUEST = 1;
        private TextView role;
        private TextView adminName;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.class_list_activity);
            Toolbar toolbar = findViewById(R.id.toolbar_layout);
            TextView toolbarTitle = toolbar.findViewById(R.id.toolbarTitle2);
            toolbarTitle.setText("UI Admin");
            setSupportActionBar(toolbar);
            context = UI_Admin.this;
            drawer = findViewById(R.id.drawer_layout);
            listView = findViewById(R.id.listView);
            expandable_navigation = findViewById(R.id.expandable_navigation);
            NavigationView navigationView = findViewById(R.id.nav_view);
            View header= navigationView.getHeaderView(0);
            role= header.findViewById(R.id.role);
            role.setText(R.string.role_admin);
            Intent intent = getIntent();
            if (intent.hasExtra("username")) {
                String username = intent.getStringExtra("username");
                adminName = header.findViewById(R.id.adminName);;
                adminName.setText(username.toUpperCase());
            }

            navigationView.setNavigationItemSelectedListener(this);
            toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Trong phương thức onItemClick
                    int classId = getClassIdForPosition(position);
                    if (classId != -1) {
                        Intent intent = new Intent(UI_Admin.this, StudentListActivityAdmin.class);
                        intent.putExtra("ClassId", classId); // Truyền class ID
                        startActivityForResult(intent, ADD_ACCOUNT_REQUEST);
                    }
                }
            });

            expandable_navigation.init(this)
                    .addHeaderModel(new HeaderModel("Home"))
                    .addHeaderModel(new HeaderModel("Log out"))
                    .build()
                    .addOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            expandable_navigation.setSelected(groupPosition);

                            //drawer.closeDrawer(GravityCompat.START);
                            if (id == 0) {
                                //Home Menu
                                Common.showToast(context, "Home Select");
                                Intent intent = new Intent(v.getContext(), UI_Admin.class);
                                startActivity(intent);
                                drawer.closeDrawer(GravityCompat.START);
                            }
                             else if (id == 1) {
                                //Wishlist Menu
                                Common.showToast(context, "Log out Selected");
                                Intent intent = new Intent(v.getContext(), AdminLogin.class);
                                startActivity(intent);
                                finish();
                            }
                            return false;
                        }
                    });

            classList = new ArrayList<>();
            backgroundList = new ArrayList<>();
            studentCountList = new ArrayList<>();
            adapter = new CustomClassListAdapterForCommon(this, classList, backgroundList, studentCountList);
            listView.setAdapter(adapter);
            connection = SQLConnection.getConnection();
            if (connection != null) {
                loadAccountData();
            } else {
                Toast.makeText(this, "Không thể kết nối đến cơ sở dữ liệu.", Toast.LENGTH_SHORT).show();
            }
            Button addAccountButton = findViewById(R.id.addAccountButton);
            addAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UI_Admin.this, ClassAddActivityForAdmin_Teacher.class);
                    startActivityForResult(intent, ADD_ACCOUNT_REQUEST);
                }
            });
        }
        @Override
        public void onBackPressed() {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here.
            int id = item.getItemId();
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                Intent intent = new Intent(UI_Admin.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        private void loadAccountData() {
            classList.clear();
            backgroundList.clear();
            studentCountList.clear();

            String query = "SELECT [id], [name_class], [name_subject], [background] FROM [PROJECT].[dbo].[CLASS]";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String name_class = resultSet.getString("name_class");
                    String name_subject = resultSet.getString("name_subject");
                    int backgroundValue = resultSet.getInt("background");
                    int studentCount = getStudentCountForClass(resultSet.getInt("id"));
                    String accountInfo = name_subject + "\n" + name_class;
                    classList.add(accountInfo);
                    backgroundList.add(backgroundValue);
                    studentCountList.add(studentCount);
                }
                resultSet.close();
                preparedStatement.close();
                adapter.notifyDataSetChanged();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi lấy dữ liệu từ cơ sở dữ liệu.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == ADD_ACCOUNT_REQUEST && resultCode == RESULT_OK) {
                loadAccountData();
            }
        }

        private int getStudentCountForClass(int classId) {
            String query = "SELECT COUNT(*) AS studentCount FROM THAMGIA WHERE classid = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, classId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("studentCount");
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }
        private int getClassIdForPosition(int position) {
            if (connection != null && position >= 0 && position < classList.size()) {
                String selectedSubject = classList.get(position).split("\n")[0];
                String query = "SELECT [id] FROM [PROJECT].[dbo].[CLASS] WHERE [name_subject] = ?";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, selectedSubject);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return -1; // Trả về -1 nếu không tìm thấy class ID
        }
        @Override
        protected void onResume() {
            super.onResume();
                loadAccountData();
        }
    }