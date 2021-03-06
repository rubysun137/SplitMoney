package com.ruby.splitmoney.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruby.splitmoney.R;
import com.ruby.splitmoney.group.GroupContract;
import com.ruby.splitmoney.objects.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter {

    private GroupContract.Presenter mPresenter;
    private List<Group> mGroupList;
    private Context mContext;

    public void setGroupList(List<Group> groups) {
        mGroupList = new ArrayList<>(groups);
        notifyDataSetChanged();
    }

    public GroupListAdapter(GroupContract.Presenter presenter) {
        mPresenter = presenter;
        mGroupList = new ArrayList<>();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        mContext = parent.getContext();

        return new GroupListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((GroupListViewHolder) holder).bindView();
    }

    @Override
    public int getItemCount() {
        return mGroupList.size() + 1;
    }

    private class GroupListViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mGroupName;
        private LinearLayout mGroupBalance;
        private TextView mMoney;
        private View mDivider;

        public GroupListViewHolder(@NonNull View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.group_icon);
            mGroupName = itemView.findViewById(R.id.group_list_name);
            mDivider = itemView.findViewById(R.id.group_divider);

        }

        private void bindView() {
            final int position = getAdapterPosition();
            if (position == mGroupList.size()) {
                mImage.setVisibility(View.INVISIBLE);
                mGroupName.setVisibility(View.INVISIBLE);
                mDivider.setVisibility(View.INVISIBLE);
//                mMoney.setVisibility(View.INVISIBLE);
            } else {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.transToGroupDetailPage(mGroupList.get(getAdapterPosition()).getId());
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                    mPresenter.deleteGroupDialog(mGroupList.get(position).getId());
                        return true;
                    }
                });
                mImage.setVisibility(View.VISIBLE);
                mGroupName.setVisibility(View.VISIBLE);
                if (position == mGroupList.size() - 1) {
                    mDivider.setVisibility(View.INVISIBLE);
                } else {
                    mDivider.setVisibility(View.VISIBLE);
                }
                mGroupName.setText(mGroupList.get(position).getName());
            }

        }
    }
}
