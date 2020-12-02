package com.thuanb1510868.mysport.View.TrangChu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.thuanb1510868.mysport.Adapter.ExpandAdapter;
import com.thuanb1510868.mysport.Adapter.ViewPagerAdapter;
import com.thuanb1510868.mysport.Model.DangNhap_DangKy.ModelDangNhap;
import com.thuanb1510868.mysport.Model.ObjectClass.LoaiSanPham;
import com.thuanb1510868.mysport.Presenter.ChiTietSanPham.PresenterLogicChiTietSanPham;
import com.thuanb1510868.mysport.Presenter.XuLyMenu.PresenterLogicXuLyMenu;
import com.thuanb1510868.mysport.R;
import com.thuanb1510868.mysport.View.DangNhap_DangKy.DangNhapActivity;
import com.thuanb1510868.mysport.View.DangNhap_DangKy.KeyHash;
import com.thuanb1510868.mysport.View.GioHang.GioHangActivity;
import com.thuanb1510868.mysport.View.TimKiem.TimKiemActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TrangChuActivity extends AppCompatActivity implements ViewXuLyMenu, GoogleApiClient.OnConnectionFailedListener, AppBarLayout.OnOffsetChangedListener{

    public static final String SERVER_NAME = "http://192.168.1.144:80/thuansv/api/loaisanpham.php";
    public static final String SERVER = "http://192.168.1.144:80/thuansv/";

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    ExpandableListView expandableListView;
    PresenterLogicXuLyMenu logicXuLyMenu;
    String tennguoidung = "";
    AccessToken accessToken;
    Menu menu;
    ModelDangNhap modelDangNhap;
    MenuItem itemDangNhap;
    MenuItem menuITDangXuat;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInResult googleSignInResult;
    TextView txtGioHang, txtSoLuongSanPhamGioHang;
    boolean onPause =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.fullyInitialize();
        setContentView(R.layout.trangchu_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        expandableListView = (ExpandableListView) findViewById(R.id.epMenu);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        logicXuLyMenu = new PresenterLogicXuLyMenu(this);

        modelDangNhap = new ModelDangNhap();

        logicXuLyMenu.LayDanhSachMenu();
        mGoogleApiClient = modelDangNhap.LayGoogleApiClient(this, this);
        //googleSignInResult = modelDangNhap.LayThongTinDangNhapGoogle(mGoogleApiClient);

        appBarLayout.addOnOffsetChangedListener(this);
        KeyHash.printKeyHash(this);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menutrangchu, menu);
        this.menu = menu;

        MenuItem iGioHang = menu.findItem(R.id.itGioHang);
        View giaoDienCustomGioHang = (View) iGioHang.getActionView();
        //View giaoDienCustomGioHang =  MenuItemCompat.getActionView(iGioHang);
        txtGioHang = (TextView) giaoDienCustomGioHang.findViewById(R.id.txtSoLuongSanPhamGioHang);

        giaoDienCustomGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGioHang = new Intent(TrangChuActivity.this, GioHangActivity.class);
                startActivity(iGioHang);
            }
        });


        PresenterLogicChiTietSanPham presenterLogicChiTietSanPham = new PresenterLogicChiTietSanPham();
        txtGioHang.setText(String.valueOf(presenterLogicChiTietSanPham.DemSanPhamCoTrongGioHang(this)));

        itemDangNhap = menu.findItem(R.id.itDangNhap);
        menuITDangXuat = menu.findItem(R.id.itDangXuat);

        accessToken = logicXuLyMenu.LayTokenDungFacebook();
        googleSignInResult = modelDangNhap.LayThongTinDangNhapGoogle(mGoogleApiClient);

        if (accessToken != null) {

            GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        tennguoidung = object.getString("name");

                        itemDangNhap.setTitle(tennguoidung);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameter = new Bundle();
            parameter.putString("fields", "name");

            graphRequest.setParameters(parameter);
            graphRequest.executeAsync();
        }

        if (googleSignInResult != null) {
            itemDangNhap.setTitle(googleSignInResult.getSignInAccount().getDisplayName());
            Log.d("Google", googleSignInResult.getSignInAccount().getDisplayName());
        }

        String tennv = modelDangNhap.LayCachedDangNhap(this);
        if (!tennv.equals("")) {
            itemDangNhap.setTitle(tennv);

        }
        if (accessToken != null || googleSignInResult != null || !tennv.equals("")) {

            menuITDangXuat.setVisible(true);

        }
        return true;
    }
        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if (drawerToggle.onOptionsItemSelected(item)) {
                return true;
            }

            int id = item.getItemId();
            switch (id) {
                case R.id.itDangNhap:
                    if (accessToken == null && googleSignInResult == null && modelDangNhap.LayCachedDangNhap(this).equals("")) {
                        Intent iDangNhap = new Intent(this, DangNhapActivity.class);
                        startActivity(iDangNhap);
                    };break;

                case  R.id.itDangXuat:
                    if (accessToken != null) {
                        LoginManager.getInstance().logOut();
                        this.menu.clear();
                        this.onCreateOptionsMenu(this.menu);
                    }

                    if (googleSignInResult != null) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        this.menu.clear();
                        this.onCreateOptionsMenu(this.menu);
                    }



                    if (!modelDangNhap.LayCachedDangNhap(this).equals("")) {
                        modelDangNhap.CapNhatCachedDangNhap(this, "");
                        this.menu.clear();
                        this.onCreateOptionsMenu(this.menu);
                    }

                    break;
                case R.id.itSearch:
                    Intent iTimKiem = new Intent(this, TimKiemActivity.class);
                    startActivity(iTimKiem);
                    break;


            }
            return true;
        }


    @Override
    public void HienThiDanhSachMenu(List<LoaiSanPham> loaiSanPhamList) {
        ExpandAdapter expandAdapter = new ExpandAdapter(this, loaiSanPhamList);
        expandableListView.setAdapter(expandAdapter);
        expandAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (collapsingToolbarLayout.getHeight() + verticalOffset <= 1.5 * ViewCompat.getMinimumHeight(collapsingToolbarLayout)) {

            LinearLayout linearLayout = (LinearLayout) appBarLayout.findViewById(R.id.lnSearch);
            linearLayout.animate().alpha(0).setDuration(200);

            MenuItem itSearch = menu.findItem(R.id.itSearch);
            itSearch.setVisible(true);
        }else {
            LinearLayout linearLayout = (LinearLayout) appBarLayout.findViewById(R.id.lnSearch);
            linearLayout.animate().alpha(1).setDuration(200);
            try {
                MenuItem itSearch = menu.findItem(R.id.itSearch);
                itSearch.setVisible(false);
            }catch (Exception e) {

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(onPause){
            PresenterLogicChiTietSanPham presenterLogicChiTietSanPham = new PresenterLogicChiTietSanPham();
            txtGioHang.setText(String.valueOf(presenterLogicChiTietSanPham.DemSanPhamCoTrongGioHang(this)));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPause = true;
    }



}

