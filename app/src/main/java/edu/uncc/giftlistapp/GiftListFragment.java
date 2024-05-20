package edu.uncc.giftlistapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;

import edu.uncc.giftlistapp.databinding.FragmentGiftListBinding;
import edu.uncc.giftlistapp.databinding.ListItemProductBinding;
import edu.uncc.giftlistapp.models.Gift;
import edu.uncc.giftlistapp.models.GiftList;
import edu.uncc.giftlistapp.models.Products;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GiftListFragment extends Fragment {

    private static final String ARG_PARAM_GIFTLIST = "ARG_PARAM_GIFTLIST";
        public static GiftListFragment newInstance(GiftList giftList) {
        GiftListFragment fragment = new GiftListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_GIFTLIST, giftList);
        fragment.setArguments(args);
        return fragment;
    }
    public GiftListFragment() {
        // Required empty public constructor
    }

    FragmentGiftListBinding binding;
    GiftList mgiftlist;
    ArrayList<Gift> mgifts = new ArrayList<>();
    GiftListAdapter adapter;
    double overAllCost;
    int totalCount;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGiftListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    String mToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mgiftlist = (GiftList) getArguments().getSerializable(ARG_PARAM_GIFTLIST);
            mgifts = mgiftlist.getItems();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Gift List");
        mToken = mListener.getAuthToken(); //token authorization

        adapter = new GiftListAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        updateTotalCost();
        binding.textViewGiftListName.setText(mgiftlist.getName());

    }

    public void updateTotalCost(){
        overAllCost = 0.0;

        for (int i = 0; i < mgifts.size(); i++) {
            Gift gift = mgifts.get(i);
            overAllCost = overAllCost + (gift.getCount() * Double.parseDouble(gift.getPrice_per_item()));

        }
        binding.textViewOverallCost.setText(String.valueOf(overAllCost));
    }

    private final OkHttpClient client = new OkHttpClient();
    public void addItem(String gid, String pid){
        RequestBody formBody = new FormBody.Builder()
                .add("gid", gid)
                .add("pid", pid)
                .build();

        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/giftlists/add-item")
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

                }else{

                }
            }
        });
    }

    public void removeItem(String gid, String pid){
        RequestBody formBody = new FormBody.Builder()
                .add("gid", gid)
                .add("pid", pid)
                .build();

        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/giftlists/remove-item")
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

                }
            }
        });
    }

    GiftListListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GiftListListener){
            mListener = (GiftListListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement GiftListListener");
        }
    }

    interface GiftListListener{
        String getAuthToken();
    }


    class GiftListAdapter extends RecyclerView.Adapter<GiftListAdapter.GiftListViewHolder>{
        @NonNull
        @Override
        public GiftListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListItemProductBinding mbinding = ListItemProductBinding.inflate(getLayoutInflater(), parent, false);
            GiftListViewHolder holder = new GiftListViewHolder(mbinding);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull GiftListViewHolder holder, int position) {
            Gift gift = mgifts.get(position);
            holder.setupUI(gift);
        }

        @Override
        public int getItemCount() {
            return mgifts.size();
        }

        class GiftListViewHolder extends RecyclerView.ViewHolder{

            ListItemProductBinding mBinding;
            public GiftListViewHolder(ListItemProductBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
            public void setupUI(Gift gift){
                mBinding.textViewName.setText(gift.getName());
                mBinding.textViewCostPerItem.setText(gift.getPrice_per_item());
                mBinding.textViewItemCount.setText(String.valueOf(gift.getCount()));

                mBinding.imageViewPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gift.incrementCount();
                        notifyDataSetChanged();
                        updateTotalCost();
//                        totalCount = Integer.parseInt(mBinding.textViewItemCount.getText().toString());
//                        totalCount += 1;
                        addItem(mgiftlist.getGid(), gift.getPid());
                    }
                });

                mBinding.imageViewMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(gift.getCount() > 0){
                            gift.decrementCount();
                            notifyDataSetChanged();
                            updateTotalCost();
                            removeItem(mgiftlist.getGid(), gift.getPid());
                        }
                    }
                });
            }
        }
    }
}