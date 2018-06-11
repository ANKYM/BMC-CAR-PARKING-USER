package com.omkar.bmccarparkinguser.Adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omkar.bmccarparkinguser.Helpers.DateFormatter;
import com.omkar.bmccarparkinguser.Model.MyBooking;
import com.omkar.bmccarparkinguser.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.getDrawable;

/**
 * Created by omkar on 27-Feb-18.
 */

public class MyBookingAdaptor extends RecyclerView.Adapter<MyBookingAdaptor.MyViewHolder> {

    private List<MyBooking> myBookingList;
    private Context context;

    public MyBookingAdaptor(Context context, ArrayList<MyBooking> myBookingList) {
        this.context = context;
        this.myBookingList = myBookingList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyBooking myBooking = myBookingList.get(position);
        holder.tv_date.setText(DateFormatter.returnDate(myBooking.getBookingTime()));
        holder.tv_time.setText(DateFormatter.returnTime(myBooking.getBookingTime()));
        holder.tv_lot_name.setText(myBooking.getLotName());
        holder.tv_vehicle_no.setText(   myBooking.getVehicleNo().toString().substring(0,2) + " " + myBooking.getVehicleNo().toString().substring(2,4) + " " + myBooking.getVehicleNo().toString().substring(4,6) + " " + myBooking.getVehicleNo().toString().substring(6) );
        if (myBooking.getVehicleType().equals("Bike")) {
            holder.iv_book_vehicle.setImageDrawable(getDrawable(context, R.drawable.bike));
        } else if (myBooking.getVehicleType().equals("Bus")) {
            holder.iv_book_vehicle.setImageDrawable(getDrawable(context, R.drawable.bus));
        } else {
            holder.iv_book_vehicle.setImageDrawable(getDrawable(context, R.drawable.car));
        }
        if (myBooking.getBookingStatus() == 0) {
            holder.ll_book_status_back.setBackground(getDrawable(context, R.drawable.booked));
            holder.tv_book_status.setText("BOOKED");
        } else if (myBooking.getBookingStatus() == 1) {
            holder.ll_book_status_back.setBackground(getDrawable(context, R.drawable.parked));
            holder.tv_book_status.setText("PARKED");
        } else if (myBooking.getBookingStatus() == -1) {
            holder.ll_book_status_back.setBackground(getDrawable(context, R.drawable.booked));
            holder.tv_book_status.setText("CANCELED");
        } else if (myBooking.getBookingStatus() == 2) {
            holder.ll_book_status_back.setBackground(getDrawable(context, R.drawable.completed));
            holder.tv_book_status.setText("COMPLETE");
        }
    }

    @Override
    public int getItemCount() {
        return myBookingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_book_status_back;
        TextView tv_book_status, tv_vehicle_no, tv_lot_name, tv_date, tv_time;
        ImageView iv_book_vehicle;

        MyViewHolder(View view) {
            super(view);
            ll_book_status_back = (LinearLayout) view.findViewById(R.id.ll_book_status_back);
            tv_book_status = (TextView) view.findViewById(R.id.tv_book_status);
            tv_vehicle_no = (TextView) view.findViewById(R.id.tv_vehicle_no);
            tv_lot_name = (TextView) view.findViewById(R.id.tv_lot_name);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            iv_book_vehicle = (ImageView) view.findViewById(R.id.iv_book_vehicle);
        }

    }

    public MyBooking getMyBookingObject(int position) {
        return myBookingList.get(position);
    }
}
