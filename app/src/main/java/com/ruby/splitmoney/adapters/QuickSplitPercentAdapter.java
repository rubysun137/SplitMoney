package com.ruby.splitmoney.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ruby.splitmoney.R;
import com.ruby.splitmoney.quicksplit.QuickSplitContract;

import java.util.ArrayList;
import java.util.List;

public class QuickSplitPercentAdapter extends RecyclerView.Adapter {
    private QuickSplitContract.Presenter mPresenter;
    private int mTotalMoney;
    private int mTotalMember;
    private List<String> mMoneyList;
    private List<String> mMemberList;

    public QuickSplitPercentAdapter(String money, String member, QuickSplitContract.Presenter presenter) {
        mTotalMoney = Integer.parseInt(money);
        mTotalMember = Integer.parseInt(member);
        mPresenter = presenter;
        mPresenter.setListSize(mTotalMember);
        mMoneyList = new ArrayList<>();
        for (int i = 0; i < mTotalMember; i++) {
            mMoneyList.add(i, "");
        }
        mMemberList = new ArrayList<>();
        for (int i = 0; i < mTotalMember; i++) {
            mMemberList.add(i, "成員" + (i + 1));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quick_dialog_percent, parent, false);

        return new QuickSplitPercentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((QuickSplitPercentViewHolder) holder).bindView();
    }

    @Override
    public int getItemCount() {
        return mTotalMember;
    }

    private class QuickSplitPercentViewHolder extends RecyclerView.ViewHolder {
        private EditText mMemberNumber;
        private EditText mSharedMoney;
        private int mPosition;

        public QuickSplitPercentViewHolder(@NonNull View itemView) {
            super(itemView);
            mMemberNumber = itemView.findViewById(R.id.partial_quick_split_percent_member);
//            mAverageMoney = itemView.findViewById(R.id.partial_spit_average_money);
            mSharedMoney = itemView.findViewById(R.id.partial_quick_split_percent_share_money);

        }

        private void bindView() {
            mPosition = getAdapterPosition();
            mMemberNumber.setText(mMemberList.get(mPosition));
            mMemberNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String member = mMemberNumber.getText().toString();
                    mMemberList.set(mPosition, member);
                    mPresenter.addMemberNameList(mPosition, member);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            mSharedMoney.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    mMoneyList.set(mPosition, mSharedMoney.getText().toString());
                    String share = mSharedMoney.getText().toString();
                    if (share.equals("")) {
                        share = "0";
                    }
                    mPresenter.addSharedMoneyList(mPosition, Integer.valueOf(share));
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mSharedMoney.setText(mMoneyList.get(mPosition));
        }
    }
}
