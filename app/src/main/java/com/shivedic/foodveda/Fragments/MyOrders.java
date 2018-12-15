package com.shivedic.foodveda.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.shivedic.foodveda.Activities.AccountVerification;
import com.shivedic.foodveda.Activities.SplashScreen;
import com.shivedic.foodveda.Adapter.MyOrdersAdapter;
import com.shivedic.foodveda.Activities.Login;
import com.shivedic.foodveda.Activities.MainActivity;
import com.shivedic.foodveda.Activities.SignUp;
import com.shivedic.foodveda.Extras.Config;
import com.shivedic.foodveda.MVP.Extra;
import com.shivedic.foodveda.MVP.MyOrdersResponse;
import com.shivedic.foodveda.MVP.OrderVariants;
import com.shivedic.foodveda.MVP.Ordere;
import com.shivedic.foodveda.R;
import com.shivedic.foodveda.Retrofit.Api;
import com.shivedic.foodveda.Volley.Volley_Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyOrders extends Fragment {

    View view;
   public static RecyclerView myorder;
   public static LinearLayout emptyorder,loginlayout,verifyemail;
   public static Button continueshopping;
  //  @BindView(R.id.myOrdersRecyclerView)
    //static RecyclerView myOrdersRecyclerView;
    public static MyOrdersResponse myOrdersResponseData;
public static Context mContext =null;
    //@BindView(R.id.emptyOrdersLayout)
    //LinearLayout emptyOrdersLayout;
   // @BindView(R.id.loginLayout)
    //LinearLayout loginLayout;
    //@BindView(R.id.continueShopping)
    //Button continueShopping;
static SweetAlertDialog ps = null;
   // @BindView(R.id.verifyEmailLayout)
   // LinearLayout verifyEmailLayout;
    static SweetAlertDialog pDialog = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        ButterKnife.bind(this, view);
        myorder = (RecyclerView) view.findViewById(R.id.myOrdersRecyclerView);
        emptyorder = (LinearLayout) view.findViewById(R.id.emptyOrdersLayout);
        loginlayout = (LinearLayout) view.findViewById(R.id.loginLayout);
        verifyemail = (LinearLayout) view.findViewById(R.id.verifyEmailLayout);
        continueshopping = (Button) view.findViewById(R.id.continueShopping);
        MainActivity.title.setText("My Orders");
        mContext = getActivity();
        if (!MainActivity.userId.equalsIgnoreCase("")) {
            getMyOrders();
        } else {
            loginlayout.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @OnClick({R.id.continueShopping, R.id.loginNow, R.id.txtSignUp, R.id.verfiyNow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continueShopping:
                ((MainActivity)getActivity()).removeCurrentFragmentAndMoveBack();
                break;
            case R.id.loginNow:
                Config.moveTo(getActivity(), Login.class);
                break;
            case R.id.txtSignUp:
                Config.moveTo(getActivity(), SignUp.class);
                break;

            case R.id.verfiyNow:
                Config.moveTo(getActivity(), AccountVerification.class);
                break;
        }
    }
    public void getMyOrders() {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        String req = "{\"res_id\":\"" + SplashScreen.resId + "\", \"user_id\":\""+ MainActivity.userId +"\"}";
        Volley_Request postRequest = new Volley_Request();
        postRequest.createRequest(getActivity(), getResources().getString(R.string.mJSONURL_viewordersdetails), "POST", "getMyOrders",req);

        /*
        Api.getClient().getMyOrders("res007", MainActivity.userId, new Callback<MyOrdersResponse>() {
            @Override
            public void success(MyOrdersResponse myOrdersResponse, Response response) {
                pDialog.dismiss();
                if (myOrdersResponse.getSuccess().equalsIgnoreCase("true")) {
                    try {
                        Log.d("size", myOrdersResponse.getOrderdata().size() + "");
                        myOrdersResponseData = myOrdersResponse;
                        setProductsData();
                    } catch (Exception e) {
                        emptyOrdersLayout.setVisibility(View.VISIBLE);
                    }
                }else {
                    verifyEmailLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                emptyOrdersLayout.setVisibility(View.VISIBLE);
                pDialog.dismiss();

            }
        });*/
    }

    public static void myOrdersResponse(String response ){
        pDialog.dismiss();
        try {
            JSONObject myOrderResp = new JSONObject(response);
            JSONArray ordereList = myOrderResp.getJSONArray("orderdata");
            List<Ordere> orderFinalList = new ArrayList<>();
            for(int i = 0; i < ordereList.length(); i++){
                JSONObject ordereObj = ordereList.getJSONObject(i);
                JSONArray orderVariants = ordereObj.getJSONArray("varients");
                List<OrderVariants> orderVariantFinalList = new ArrayList<>();
                for(int j=0; j<orderVariants.length(); j++){
                    JSONObject orderVarObj = orderVariants.getJSONObject(j);
                    List<Extra> extraFinalList = new ArrayList<>();
                    if(!orderVarObj.isNull("extra")){
                    JSONArray extrasList = orderVarObj.getJSONArray("extra");
                    for(int k=0; k<extrasList.length(); k++){
                        JSONObject extraObj = extrasList.getJSONObject(k);
                        extraFinalList.add(new Extra(extraObj.getString("extraid"),extraObj.getString("extraname"),extraObj.getString("extraprice"),extraObj.getString("extraquantity")));
                    }}
                    orderVariantFinalList.add(new OrderVariants(orderVarObj.getString("varientid"),orderVarObj.getString("variantname"),orderVarObj.getString("varquantity"),orderVarObj.getString("varprice"),orderVarObj.getString("product_id"),orderVarObj.getString("productname"),orderVarObj.getString("image"),extraFinalList));
                }
                orderFinalList.add(new Ordere(ordereObj.getString("orderid"),ordereObj.getString("paymode"),"",ordereObj.getString("paymentstatus"),ordereObj.getString("orderdate"),ordereObj.getString("total"),ordereObj.getString("delivery"),ordereObj.getString("tax"),ordereObj.getString("address"),ordereObj.getString("currency") ,orderVariantFinalList));
            }
            MyOrdersResponse myOrdersResponse = new MyOrdersResponse("7","","","",orderFinalList );
            myOrdersResponseData = myOrdersResponse;
            setProductsData();
        } catch (Exception ex ){
            Log.d("myTag", "error : " , ex);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).lockUnlockDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        Config.getCartList(getActivity(), true);
    }

    private static void setProductsData() {
        GridLayoutManager gridLayoutManager;
        gridLayoutManager = new GridLayoutManager(mContext, 1);
        myorder.setLayoutManager(gridLayoutManager);
        MyOrdersAdapter myOrdersAdapter = new MyOrdersAdapter(mContext, myOrdersResponseData.getOrderdata());
        myorder.setAdapter(myOrdersAdapter);

    }
}