package edu.uncc.giftlistapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import edu.uncc.giftlistapp.databinding.FragmentGiftListsBinding;
import edu.uncc.giftlistapp.databinding.ListItemGiftlistBinding;
import edu.uncc.giftlistapp.models.Gift;
import edu.uncc.giftlistapp.models.GiftList;
import edu.uncc.giftlistapp.models.GiftRoot;
import edu.uncc.giftlistapp.models.ProductRoot;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GiftListsFragment extends Fragment {
    public GiftListsFragment() {
        // Required empty public constructor
    }

    FragmentGiftListsBinding binding;
    ArrayList<GiftList> mGiftList = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    GiftsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGiftListsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    String mToken;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Gift Lists");
        mToken = mListener.getAuthToken(); //token authorization

        adapter = new GiftsAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        getGiftList();


    }

    public void getGiftList(){
        mGiftList.clear();
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/giftlists/lists")
                .addHeader("Authorization", "BEARER "  + mToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String body = response.body().string();
                    Gson gson = new Gson();
                    GiftRoot giftRoot = gson.fromJson(body, GiftRoot.class);
                    mGiftList.addAll(giftRoot.getLists());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }else{

                }
            }
        });
    }
    public void delete(String gid){
        RequestBody formBody = new FormBody.Builder()
                .add("gid", gid)
                .build();

        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/giftlists/delete")
                .addHeader("Authorization", "BEARER "  + mToken)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    getGiftList();
                }else{

                }
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout){
            mListener.performLogout();
            return true;
        } else if (item.getItemId() == R.id.action_add){
            mListener.gotoAddNewGiftList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    GiftListsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GiftListsListener){
            mListener = (GiftListsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement GiftListsListener");
        }
    }

    interface GiftListsListener{
        String getAuthToken();
        void gotoAddNewGiftList();
        void performLogout();
        void gotoListDetails(GiftList giftList);
    }

    class GiftsAdapter extends RecyclerView.Adapter<GiftsAdapter.GiftsViewHolder>{

        @NonNull
        @Override
        public GiftsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListItemGiftlistBinding mbinding = ListItemGiftlistBinding.inflate(getLayoutInflater(), parent, false);
            GiftsViewHolder holder = new GiftsViewHolder(mbinding);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull GiftsViewHolder holder, int position) {
//            Gift gift = mGiftList.get(position);
            GiftList giftList = mGiftList.get(position);
            holder.setupUI(giftList);
        }

        @Override
        public int getItemCount() {
            return mGiftList.size();
        }

        class GiftsViewHolder extends RecyclerView.ViewHolder{

            ListItemGiftlistBinding mBinding;
            GiftList mGiftList;
            public GiftsViewHolder(ListItemGiftlistBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(GiftList giftList){
                mGiftList = giftList;

                mBinding.textViewName.setText(mGiftList.getName());
                ArrayList<Gift> items = mGiftList.getItems();
                double totalPrice = 0.0;
                int count = 0;
                for (int i = 0; i < items.size(); i++) {
                    Gift gift = items.get(i);
                    count += gift.getCount();
                    totalPrice += gift.getCount() * Double.parseDouble(gift.getPrice_per_item());
                }
                mBinding.textViewTotalItems.setText(String.valueOf(count));
                mBinding.textViewTotalCost.setText(String.valueOf(totalPrice));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.gotoListDetails(mGiftList);
                    }
                });
                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete(mGiftList.getGid());
                    }
                });
            }
        }
    }
}