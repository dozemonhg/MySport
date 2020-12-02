package com.thuanb1510868.mysport.View.TimKiem;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thuanb1510868.mysport.Adapter.AdapterTopAoTheThao;
import com.thuanb1510868.mysport.Model.ObjectClass.ILoadMore;
import com.thuanb1510868.mysport.Model.ObjectClass.LoadMoreScroll;
import com.thuanb1510868.mysport.Model.ObjectClass.SanPham;
import com.thuanb1510868.mysport.Presenter.TimKiem.PresenterLogicTimKiem;
import com.thuanb1510868.mysport.R;

import java.util.List;

public class TimKiemActivity extends AppCompatActivity implements ViewTimKiem, ILoadMore, SearchView.OnQueryTextListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    PresenterLogicTimKiem presenterLogicTimKiem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_timkiem);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerTimKiem);

        setSupportActionBar(toolbar);

        presenterLogicTimKiem = new PresenterLogicTimKiem(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_timkiem, menu);
       // SearchManager  = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
       //SearchView searchView = (SearchView) menu.findItem(R.id.itSearch).getActionView();

        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        getMenuInflater().inflate(R.menu.menu_timkiem,menu);

        MenuItem itSearch = menu.findItem(R.id.itSearch);

        SearchView searchView = (SearchView) itSearch.getActionView();
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(itSearch);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public void TimKiemThanhCong(List<SanPham> sanPhamList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        AdapterTopAoTheThao adapterTopAoTheThao = new AdapterTopAoTheThao(this,R.layout.custom_layout_list_topaovaquan,sanPhamList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterTopAoTheThao);
        recyclerView.addOnScrollListener(new LoadMoreScroll(layoutManager,this));
        adapterTopAoTheThao.notifyDataSetChanged();
    }

    @Override
    public void TimKiemThatBai() {

    }

    @Override
    public void LoadMore(int tongitem) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        presenterLogicTimKiem.TimKiemSanPhamTheoTenSP(query,0);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
