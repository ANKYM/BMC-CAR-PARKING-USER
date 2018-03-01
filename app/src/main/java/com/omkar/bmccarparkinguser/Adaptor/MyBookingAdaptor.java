package com.omkar.bmccarparkinguser.Adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    public MyBookingAdaptor(Context context,ArrayList<MyBooking> myBookingList) {
        this.context =context;
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
        holder.tv_booking_time.setText(myBooking.getBookingTime());
        holder.tv_lotName.setText(myBooking.getLotId());
        holder.tv_booking_amount.setText(myBooking.getOwnerMobileNo());
        if(myBooking.getBookConfrimed()==0)
        {
            holder.iv_book_status.setImageDrawable(getDrawable(context,R.drawable.p));
        }else
        {
            holder.iv_book_status.setImageDrawable(getDrawable(context,R.drawable.c));
            holder.iv_book_status.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    @Override
    public int getItemCount() {
        return myBookingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_booking_time, tv_lotName, tv_booking_amount;
        public ImageView iv_book_status;

        public MyViewHolder(View view) {
            super(view);
            tv_booking_time = (TextView) view.findViewById(R.id.tv_booking_time);
            tv_lotName = (TextView) view.findViewById(R.id.tv_lotName);
            tv_booking_amount = (TextView) view.findViewById(R.id.tv_booking_amount);
            iv_book_status = (ImageView) view.findViewById(R.id.iv_book_status);

        }

    }
}
