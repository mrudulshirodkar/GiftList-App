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
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.uncc.giftlistapp.databinding.FragmentCreateGiftListBinding;
import edu.uncc.giftlistapp.databinding.ListItemProductBinding;
import edu.uncc.giftlistapp.models.ProductRoot;
import edu.uncc.giftlistapp.models.Products;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateGiftListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGiftListFragment extends Fragment {

    public CreateGiftListFragment() {
        // Required empty public constructor
    }

    FragmentCreateGiftListBinding binding;
    ArrayList<Products> mProducts = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateGiftListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    String mToken;
    ProductsAdapter adapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Create Gift List");
        mToken = mListener.getAuthToken(); //token authorization

        adapter = new ProductsAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        getProducts();
        updateOverallCost(totalprice);

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editTextGiftListName.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(getActivity(), "Enter name", Toast.LENGTH_SHORT).show();
                } else if (totalprice == 0.0) {
                    Toast.makeText(getActivity(), "Select Products", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < ids.size(); i++) {
                        stringBuilder.append(ids.get(i));
                        if (i < ids.size() - 1) {
                            stringBuilder.append(",");
                        }
                    }
                    String result = stringBuilder.toString();
                    submit(name, result);
                }
            }
        });

    }

    private void updateOverallCost(double totalprice) {
        DecimalFormat df = new DecimalFormat("#.##");

        // Format the value using the DecimalFormat object
        String formattedValue = df.format(totalprice);
        binding.textViewOverallCost.setText(formattedValue);
    }

    private void getProducts() {
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/giftlists/products")
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
                    ProductRoot productRoot = gson.fromJson(body, ProductRoot.class);
                    mProducts.addAll(productRoot.getProducts());

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
    public void submit(String name, String productids){
        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("productIds", productids)
                .build();

        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/giftlists/new")
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
                    mListener.submit();
                }else{

                }
            }
        });
    }
    CreateGiftListListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (CreateGiftListListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CreateGiftListListener");
        }
    }

    interface CreateGiftListListener{
        String getAuthToken();
        void submit();
    }

    double totalprice = 0.0;
    class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>{

        @NonNull
        @Override
        public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListItemProductBinding mbinding = ListItemProductBinding.inflate(getLayoutInflater(), parent, false);
            ProductsViewHolder holder = new ProductsViewHolder(mbinding);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
            Products products = mProducts.get(position);
            holder.setupUI(products);
        }

        @Override
        public int getItemCount() {
            return mProducts.size();
        }

        class ProductsViewHolder extends RecyclerView.ViewHolder{
            ListItemProductBinding mBinding;
            public ProductsViewHolder(ListItemProductBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Products productlist){
                mBinding.textViewName.setText(productlist.getName());
                mBinding.textViewCostPerItem.setText(productlist.getPrice());
                Picasso.get().load(productlist.getImg_url()).into(mBinding.imageViewIcon);

                mBinding.imageViewPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count = Integer.parseInt(mBinding.textViewItemCount.getText().toString());
                        count += 1;
                        double price = Double.parseDouble(mBinding.textViewCostPerItem.getText().toString());
                        totalprice += price;

                        mBinding.textViewItemCount.setText(String.valueOf(count));
                        updateOverallCost(totalprice);
                        ids.add(productlist.getPid());
                        mBinding.imageViewMinus.setClickable(true);

                    }


                });

                mBinding.imageViewMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count = Integer.parseInt(mBinding.textViewItemCount.getText().toString());
                        count -= 1;
                        double price = Double.parseDouble(mBinding.textViewCostPerItem.getText().toString());
                        totalprice -= price;

                        mBinding.textViewItemCount.setText(String.valueOf(count));
                        if(ids.contains(productlist.getPid())){
                            ids.remove(productlist.getPid());
                        }
                        updateOverallCost(totalprice);
                        if(count == 0){
                            mBinding.imageViewMinus.setClickable(false);
                        }
                    }


                });

            }

        }
    }

}