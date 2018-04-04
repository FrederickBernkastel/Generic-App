package com.example.frederic.genericapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frederic.genericapp.data.get.AsyncFetchResponse;
import com.example.frederic.genericapp.data.DatabaseConnector;
import com.example.frederic.genericapp.data.get.DurationResponse;
import com.example.frederic.genericapp.data.get.FetchedObject;
import com.example.frederic.genericapp.data.get.FoodStatus;
import com.example.frederic.genericapp.data.get.FoodStatuses;
import com.example.frederic.genericapp.data.get.MenuItem;
import com.example.frederic.genericapp.data.get.RestaurantMenu;
import com.example.frederic.genericapp.R;
import com.example.frederic.genericapp.SharedPrefManager;
import com.example.frederic.genericapp.data.get.TransactionStatus;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.CardInfo;
import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.stripe.android.model.Token;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;


public class MyBillActivity extends Activity implements AsyncFetchResponse{

    private GridView gridView;
    private TextView totalPrice;
    private TextView durationText;


    private PaymentsClient paymentsClient;
    private final int LOAD_PAYMENT_DATA_REQUEST_CODE = 1;

    private boolean allowBilling;
    private String plid;
    private int tableNo;
    private double durationPrice;
    private double itemPrice;
    private RestaurantMenu menu;
    private ReentrantLock paymentLock = new ReentrantLock();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bill);

        gridView = findViewById(R.id.my_bill_activity_grid);
        totalPrice = findViewById(R.id.my_bill_activity_price);
        durationText = findViewById(R.id.my_bill_activity_duration);

        plid = new SharedPrefManager<String>().fetchObj(getString(R.string.key_plid),MyBillActivity.this,String.class);
        tableNo = new SharedPrefManager<Integer>().fetchObj(getString(R.string.key_table_no),MyBillActivity.this,Integer.class);
        menu = new SharedPrefManager<RestaurantMenu>().fetchObj(getString(R.string.key_restaurant_menu),MyBillActivity.this,RestaurantMenu.class);


         paymentsClient = Wallet.getPaymentsClient(this,
                            new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                .build());


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Prevent billing until latest fetch has finished
        allowBilling = false;

        // Extract stored information
        boolean isDurationBased = new SharedPrefManager<Boolean>().fetchObj(
                getString(R.string.key_is_duration_based),
                MyBillActivity.this,
                Boolean.class
        );

        /*
        // TODO: INSERT RESTAURANT MENU FOR DEBUGGING, DELETE WHEN DONE
        JSONArray array = new JSONArray();
        JSONObject json=new JSONObject();
        FoodStatuses foodStatuses = new FoodStatuses();
        try {
            json.put("name", "Ugandan Cuisine");
            json.put("imagehyperlink", "http://a57.foxnews.com/media2.foxnews.com/2016/06/09/640/360/060916_chew_crispychicken_1280.jpg");
            JSONObject obj1 = new JSONObject();
            obj1.put("food_id", 999);
            obj1.put("price", "5.00");
            obj1.put("currency", "S$*");
            obj1.put("name", "Baagaa");
            obj1.put("description", "Meat so fresh, you can still hear it baa-ing");
            obj1.put("image_link", "https://lh3.googleusercontent.com/alX3SlsbUt4ZBZ1ct6efz5wxIcjM6S3Gva_pstMNXGjlFAQRr6CbpwFyFNoixBgPOGXxQi7vqC3U0CDT8oGz4lu4IZWzifs40owj_jA=w600-l68");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id", 1000);
            obj1.put("price", "1.99");
            obj1.put("currency", "S$*");
            obj1.put("name", "Fries");
            obj1.put("description", "The straightest thing you'll put in your mouth every year");
            obj1.put("image_link", "https://chiosrotisserie.com/wp-content/uploads/2017/07/fries.jpg");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id", 1001);
            obj1.put("price", "1.49");
            obj1.put("currency", "S$*");
            obj1.put("name", "Coca-cola");
            obj1.put("description", "Is pepsi OK?");
            obj1.put("image_link", "https://i5.walmartimages.com/asr/791c580c-9a80-4d53-b972-50c78a935d72_1.8a4d4ced51a177d1c4dbbfb823d696f5.jpeg");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id", 1002);
            obj1.put("price", "0.99");
            obj1.put("currency", "S$*");
            obj1.put("name", "Chocolate Soft-serve Ice-cream");
            obj1.put("description", "‎💩");
            obj1.put("image_link", "http://dlitesshoppe.com/wp-content/uploads/2015/03/Dlites_chocolate.png");
            array.put(obj1);
            obj1 = new JSONObject();
            obj1.put("food_id", 1003);
            obj1.put("price", "450.00");
            obj1.put("currency", "S$*");
            obj1.put("name", "Duluxe Pizza Pie (Foie grass, truffles, cavier)");
            obj1.put("description", "Can't decide between junk food and fine dining? Look no further, for our Duluxe Pizza Pie is sure to satisfy your cravings. Topped with 24-karat gold flakes, Foie grass, truffles, cavier, and baked with the finest cheese fresh from a bull's udder, this meal is sure to help you leave your toilet shinier than before you used it.\n\n\nNote: Price per slice, not pizza");
            obj1.put("image_link", "http://finedininglovers.cdn.crosscast-system.com/BlogPost/l_7853_expensive.pizza-2.jpg");
            array.put(obj1);
            json.put("menu", array);
            String s = json.toString();
            RestaurantMenu savedMenu = DatabaseConnector.parseJSONMenu(s);
            new SharedPrefManager<RestaurantMenu>().saveObj(getString(R.string.key_restaurant_menu), savedMenu, MyBillActivity.this);
            foodStatuses = new FoodStatuses();
            foodStatuses.addStatus(999, true);
            foodStatuses.addStatus(999, true);
            foodStatuses.addStatus(1002, true);
            foodStatuses.addStatus(1001, true);
            foodStatuses.addStatus(1003, true);
            foodStatuses.addStatus(1000, true);

        } catch(Exception e){
            System.out.println("Debug failure");
        }
        refreshExistingOrders(foodStatuses);
        // END OF DEBUG*/
        try {
            DatabaseConnector.FetchTaskInput input = new DatabaseConnector.FetchTaskInput(plid, DatabaseConnector.FetchMode.EXISTINGORDERS);
            new DatabaseConnector.FetchTask(MyBillActivity.this).execute(input);
            if (isDurationBased){
                input = new DatabaseConnector.FetchTaskInput(plid,tableNo,DatabaseConnector.FetchMode.DURATION);
                new DatabaseConnector.FetchTask(MyBillActivity.this).execute(input);
            }

        } catch (Exception e){
            System.out.println("Error parsing DatabaseConnector input in MyBillActivity. Was the correct mode used?");
            // Terminate this activity
            this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void refreshExistingOrders(FoodStatuses foodStatuses){
        // Extract restaurant menu
        RestaurantMenu menu = new SharedPrefManager<RestaurantMenu>().fetchObj(getString(R.string.key_restaurant_menu),MyBillActivity.this,RestaurantMenu.class);

        // Reset views
        GridAdapter gridAdapter = new GridAdapter(MyBillActivity.this,foodStatuses,menu);
        gridView.setAdapter(gridAdapter);
        itemPrice = gridAdapter.totalPrice;
        totalPrice.setText(menu.formatPrice(itemPrice+durationPrice));
    }

    @Override
    public void fetchFinish(FetchedObject output) {

        if (output==null){
            // Error connecting to server, send to ErrorActivity
            Intent intent = new Intent(MyBillActivity.this, ErrorActivity.class);
            startActivity(intent);
            ErrorActivity.errorType = ErrorActivity.ErrorType.NOCONNECTION;
            return;
        }
        switch (output.fetchMode){
            case EXISTINGORDERS:
                FoodStatuses foodStatuses = (FoodStatuses) output;

                // DEBUG / TESTING ONLY
                // Inform user that not all orders have been fulfilled with a DialogFragment
                if (! foodStatuses.isAllFulfilled() &&false){
                    displayBillDeniedAlert();
                } else {
                    // Allow billing
                    allowBilling = true;

                    // Refresh gridView and update price
                    refreshExistingOrders(foodStatuses);
                }
                break;
            case STRIPEPAYMENT:
                TransactionStatus transactionStatus = (TransactionStatus) output;
                if (transactionStatus.success){
                    Toast.makeText(MyBillActivity.this,"Transaction Success",Toast.LENGTH_LONG).show();
                    // Go back to the MainActivity
                    Intent intent = new Intent(MyBillActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayBillFailedAlert();
                    paymentLock.unlock();
                }
                break;
            case DURATION:
                DurationResponse durationResponse = (DurationResponse) output;
                durationText.setText(String.format(
                        Locale.US,"%d hours %d minutes / %s",
                        durationResponse.hours,
                        durationResponse.minutes,
                        menu.formatPrice(durationResponse.price)
                ));
                durationText.setVisibility(View.VISIBLE);

                // Update total price
                durationPrice = durationResponse.price;
                totalPrice.setText(menu.formatPrice(durationPrice+itemPrice));
        }
    }

    // Handle billing
    public void onBillingRequestPress(View v){
        // Check if a fetch has been done
        if(allowBilling){
            // DEBUG / TESTING
            chargeToken("tok_visa");
            // Remove block for live
            /*
            PaymentDataRequest request = createPaymentDataRequest();
            if (request != null) {
                AutoResolveHelper.resolveTask(
                        paymentsClient.loadPaymentData(request),
                        this,
                        LOAD_PAYMENT_DATA_REQUEST_CODE);
                // LOAD_PAYMENT_DATA_REQUEST_CODE is a constant integer of your choice,
                // similar to what you would use in startActivityForResult
            }
            */
        }
    }

    /**
     * Stripe fuction to see whether or not to display Google Pay as an option.
     */
    private void isReadyToPay() {
        IsReadyToPayRequest request = IsReadyToPayRequest.newBuilder()
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .build();
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    public void onComplete(Task<Boolean> task) {
                        try {
                            boolean result =
                                    task.getResult(ApiException.class);
                            if(result) {
                                //show Google as payment option
                            } else {
                                //hide Google as payment option
                            }
                        } catch (ApiException exception) { }
                    }
                });
    }

    /**
     * Provides the necessary information to support a payment.
     * @return PaymentMethodTokenizationParameters representing a request for methods of payment (such as a credit card) and other details.
     */
    private PaymentMethodTokenizationParameters createTokenizationParameters() {
        return PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                .addParameter("gateway", "stripe")
                .addParameter("stripe:publishableKey", "pk_test_ch1GxUFVpWN0R0TljLM5aM1a")
                .addParameter("stripe:version", "5.1.0")
                .build();
    }

    /**
     * Procides necessary information regarding a transaction
     * @return PaymentDataRequest object with the information relevant to the purchase.
     */
    private PaymentDataRequest createPaymentDataRequest() {
        // TODO: Set Currency Code and Price
        PaymentDataRequest.Builder request =
                PaymentDataRequest.newBuilder()
                        .setTransactionInfo(
                                TransactionInfo.newBuilder()
                                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                                        .setTotalPrice("10.00")
                                        .setCurrencyCode("USD")
                                        .build())
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                        .setCardRequirements(
                                CardRequirements.newBuilder()
                                        .addAllowedCardNetworks(Arrays.asList(
                                                WalletConstants.CARD_NETWORK_AMEX,
                                                WalletConstants.CARD_NETWORK_DISCOVER,
                                                WalletConstants.CARD_NETWORK_VISA,
                                                WalletConstants.CARD_NETWORK_MASTERCARD))
                                        .build());

        request.setPaymentMethodTokenizationParameters(createTokenizationParameters());
        return request.build();
    }


    /**
     * Handles result of launching Google Pay
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        // You can get some data on the user's card, such as the brand and last 4 digits
                        CardInfo info = paymentData.getCardInfo();
                        // You can also pull the user address from the PaymentData object.
                        UserAddress address = paymentData.getShippingAddress();
                        // This is the raw JSON string version of your Stripe token.
                        String rawToken = paymentData.getPaymentMethodToken().getToken();

                        // Now that you have a Stripe token object, charge that by using the id
                        Token stripeToken = Token.fromString(rawToken);
                        if (stripeToken != null) {
                            // This chargeToken function is a call to your own server, which should then connect
                            // to Stripe's API to finish the charge.
                            chargeToken(stripeToken.getId());
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        // Log the status for debugging
                        Log.e("GooglePay Error",status.getStatusMessage());
                        // Generally there is no need to show an error to
                        // the user as the Google Payment API will do that
                        break;
                    default:
                        // Do nothing.
                }
                break; // Breaks the case LOAD_PAYMENT_DATA_REQUEST_CODE
            // Handle any other startActivityForResult calls you may have made.
            default:
                // Do nothing.
        }
    }

    /**
     * Passes tokenID to server
     */
    private void chargeToken(String tokenID){
        // Send tokenID to server
        if( paymentLock.tryLock()) {
            DatabaseConnector.FetchTaskInput input;
            try {
                input = new DatabaseConnector.FetchTaskInput(plid, tokenID, DatabaseConnector.FetchMode.STRIPEPAYMENT);
            } catch (Exception e) {
                System.out.println("Invalid FetchTaskInput parameters in MyBillActivity");
                return;
            }
            new DatabaseConnector.FetchTask(MyBillActivity.this).execute(input);
        }
    }

    /**
     * Creates a DialogFragment to inform the user that Billing is denied
     */
    public void displayBillDeniedAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(MyBillActivity.this).create();
        alertDialog.setTitle(getString(R.string.alert_billing_denied_title));
        alertDialog.setMessage(getString(R.string.alert_billing_denied_pending_orders));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBackPressed();
                    }
                });
        alertDialog.show();
    }
    /**
     * Creates a DialogFragment to inform the user that Billing is not possible
     */
    public void displayBillFailedAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(MyBillActivity.this).create();
        alertDialog.setTitle(getString(R.string.alert_billing_failed_title));
        alertDialog.setMessage(getString(R.string.alert_billing_failed_description));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBackPressed();
                    }
                });
        alertDialog.show();
    }
    
}

/**
 * Adapter used to display ordered items dynamically in rows
 */
class GridAdapter extends BaseAdapter{
    private Context context;
    private String[] gridValues;
    private static final String[] GRID_HEADERS = new String[]{
            "Item",
            "Qty",
            "Price"
    };
    Double totalPrice;


    GridAdapter(Context context, FoodStatuses foodStatuses,RestaurantMenu menu){
        // Init
        gridValues = new String[3+foodStatuses.statuses.size()*3];
        int i = 0;
        totalPrice = 0.;
        this.context = context;

        // Add headers
        for (;i<GRID_HEADERS.length;i++){
            gridValues[i] = GRID_HEADERS[i];
        }

        // Add Food name / quantity / price
        for (FoodStatus foodStatus: foodStatuses.statuses){
            MenuItem item = menu.findItem(foodStatus.food_id);
            gridValues[i++] = item.name;
            gridValues[i++] = String.valueOf(foodStatus.delivered);


            gridValues[i++] = item.formatPrice(foodStatus.totalPrice);
            totalPrice += foodStatus.totalPrice;


        }

    }

    /**
     *
     * @param i             Position of grid cell, starts from 0 in upper left
     * @param view          View in grid, which may already exist
     * @param viewGroup     Parent view
     * @return              View to be in this cell
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        // Check if view already exists
        if (view == null) {
            // Inflate new xml file, and
            LayoutInflater  inflater = LayoutInflater.from(context);
            View row = inflater.inflate(R.layout.row_my_bill_activity,null);
            TextView nameTextView = row.findViewById(R.id.row_my_bill_activity_name);
            TextView quantityTextView = row.findViewById(R.id.row_my_bill_activity_qty);
            TextView priceTextView = row.findViewById(R.id.row_my_bill_activity_price);

            // Set text
            nameTextView.setText(gridValues[i*3]);
            quantityTextView.setText(gridValues[i*3+1]);
            priceTextView.setText(gridValues[i*3+2]);

            // Check if headers
            if (i==0){
                nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                quantityTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                priceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);

                quantityTextView.setTextColor(Color.parseColor("#000000"));
                priceTextView.setTextColor(Color.parseColor("#000000"));

            }


            return row;


        } else {
            return view;
        }


    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        return gridValues.length/3;
    }


}
