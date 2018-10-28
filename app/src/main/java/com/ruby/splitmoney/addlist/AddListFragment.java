package com.ruby.splitmoney.addlist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ruby.splitmoney.R;
import com.ruby.splitmoney.adapters.AddSplitMemberAdapter;
import com.ruby.splitmoney.adapters.QuickSplitPartialAdapter;
import com.ruby.splitmoney.adapters.QuickSplitPercentAdapter;
import com.ruby.splitmoney.adapters.SplitFreeAdapter;
import com.ruby.splitmoney.adapters.SplitPartialAdapter;
import com.ruby.splitmoney.adapters.SplitPercentAdapter;
import com.ruby.splitmoney.objects.Friend;
import com.ruby.splitmoney.objects.Group;
import com.ruby.splitmoney.quicksplit.QuickSplitFragment;
import com.ruby.splitmoney.util.FriendList;
import com.ruby.splitmoney.util.WrapLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddListFragment extends Fragment implements AddListContract.View, View.OnClickListener {

    private AddListContract.Presenter mPresenter;
    private Context mContext;
    private TextView mPickDate;
    private WrapLayout mAddMemberLayout;
    private ImageView mAddMemberIcon;
    private ImageView mCalendarIcon;
    private int mLayoutId;
    private List<Friend> mNotAddFriends;
    private List<Friend> mAddedFriends;
    private AddSplitMemberAdapter mMemberAdapter;
    private TextView mFriendName;
    private ConstraintLayout mItemNameLayout;
    private Dialog mFriendDialog;
    private Map<Integer, String> mMap;
    private Spinner mPayerSpinner;
    private TextView mCancelTextButton;
    private TextView mSaveTextButton;
    private String[] mSplitType;
    private String mSelectSplitType;
    private Spinner mSplitTypeSpinner;
    private EditText mTotalMoney;
    private String mMoney;
    private View mDialogView;
    private Dialog mDialogPartial;
    private SplitPartialAdapter mPartialAdapter;
    private SplitPercentAdapter mPercentAdapter;
    private SplitFreeAdapter mFreeAdapter;
    private EditText mTipPercent;
    private EditText mEvent;
    private String mWhoPays;
    private List<String> mNames;
    private Spinner mGroupSpinner;
    private List<Group> mGroup;
    private ImageView mFriendImage;


    public AddListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_list, container, false);
        mContext = container.getContext();
        mPresenter = new AddListPresenter(this);

        mAddedFriends = new ArrayList<>();
        mNotAddFriends = new ArrayList<>(FriendList.getInstance().getFriendList());

        mPayerSpinner = view.findViewById(R.id.payer_spinner);
        mSplitTypeSpinner = view.findViewById(R.id.add_list_split_type_spinner);
        mGroupSpinner = view.findViewById(R.id.group_spinner);

        mLayoutId = 0;
        mMap = new HashMap<>();
        mAddMemberLayout = view.findViewById(R.id.add_list_member_linearlayout);
        mAddMemberIcon = view.findViewById(R.id.add_list_plus);

        mPickDate = view.findViewById(R.id.add_list_pick_day);
        mCalendarIcon = view.findViewById(R.id.add_list_calendar_icon);

        mEvent = view.findViewById(R.id.split_event);
        mTotalMoney = view.findViewById(R.id.add_list_split_money);
        mTipPercent = view.findViewById(R.id.split_tip_percent);

        mCancelTextButton = view.findViewById(R.id.add_list_cancel);
        mSaveTextButton = view.findViewById(R.id.add_list_save);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.start();
        mPresenter.getCurrentDate();
        mPresenter.getGroups();

        mPickDate.setOnClickListener(this);
        mCalendarIcon.setOnClickListener(this);
        //加號可按
        mAddMemberIcon.setOnClickListener(mAddMemberClickListener);
        mCancelTextButton.setOnClickListener(this);
        mSaveTextButton.setOnClickListener(this);

        mSplitTypeSpinner.setOnItemSelectedListener(mItemSelectedListener);

        mSplitType = new String[]{"全部均分", "部份均分", "比例分攤", "任意分配"};
        ArrayAdapter<String> splitTypeList = new ArrayAdapter<>(mContext, R.layout.dropdown_style, mSplitType);
        mSplitTypeSpinner.setAdapter(splitTypeList);

    }

    @Override
    public void setPresenter(AddListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void changeToEvenSplitType() {
        mSplitTypeSpinner.setSelection(0);
        mPresenter.selectSplitType(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_list_calendar_icon:
            case R.id.add_list_pick_day:
                new DatePickerDialog(mContext, R.style.datePicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String format = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(dayOfMonth);
                        mPickDate.setText(format);
                    }
                },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.add_list_cancel:
                getFragmentManager().popBackStack();
                break;
            case R.id.add_list_save:
                if (mAddedFriends.size() != 0 && parseInt(mTotalMoney.getText().toString()) != 0 && !mEvent.getText().toString().equals("")) {
                    mPresenter.saveSplitResultToFirebase(mEvent.getText().toString(), mAddedFriends, mWhoPays, parseInt(mTotalMoney.getText().toString()), parseInt(mTipPercent.getText().toString()), mPickDate.getText().toString(), getFragmentManager());
                } else if (mAddedFriends.size() == 0) {
                    Toast.makeText(getContext(), "至少需選擇一位朋友參與拆帳", Toast.LENGTH_SHORT).show();
                } else if (mEvent.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "請輸入拆帳標題", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "請輸入拆帳金額", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.dialog_correct_text:
                if (mSplitTypeSpinner.getSelectedItemPosition() == 2) {
                    if (mPresenter.isSharedListEmpty()) {
                        Toast.makeText(getContext(), "須至少一人分攤帳務", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if (mSplitTypeSpinner.getSelectedItemPosition() == 3) {
                    if (mPresenter.freeTotalMoney() > parseInt(mTotalMoney.getText().toString())) {
                        Toast.makeText(getContext(), "總數大於總金額 " + mTotalMoney.getText().toString() + " 元", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (mPresenter.freeTotalMoney() < parseInt(mTotalMoney.getText().toString())) {
                        Toast.makeText(getContext(), "總數小於總金額 " + mTotalMoney.getText().toString() + " 元", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                mDialogPartial.dismiss();
                break;
            case R.id.dialog_cancel_text:
                mDialogPartial.dismiss();
                mSplitTypeSpinner.setSelection(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void showSplitFriendView(int position) {
        mFriendDialog.dismiss();
        mFriendName.setText(mNotAddFriends.get(position).getName());
        if (mNotAddFriends.get(position).getImage() != null) {
            Glide.with(mContext).load(String.valueOf(mNotAddFriends.get(position).getImage())).into(mFriendImage);
        } else {
            mFriendImage.setImageResource(R.drawable.user2);
        }
        mItemNameLayout.setId(mLayoutId);
        mMap.put(mLayoutId, mNotAddFriends.get(position).getUid());
        mItemNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNotAddFriends.size() == 0) {
                    mAddMemberIcon.setVisibility(View.VISIBLE);
                }
                for (Friend friend : mAddedFriends) {
                    if (friend.getUid().equals(mMap.get(v.getId()))) {
                        mNotAddFriends.add(friend);
                        Collections.sort(mNotAddFriends, new Comparator<Friend>() {
                            @Override
                            public int compare(Friend o1, Friend o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                        mAddedFriends.remove(friend);
                        break;
                    }
                }
                mAddMemberLayout.removeView(v);
                refreshPayerSpinner();
                changeToEvenSplitType();
            }
        });
        mAddMemberLayout.addView(mItemNameLayout, mAddMemberLayout.getChildCount() - 1);
        mAddedFriends.add(mNotAddFriends.get(position));
        Collections.sort(mAddedFriends, new Comparator<Friend>() {
            @Override
            public int compare(Friend o1, Friend o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        mNotAddFriends.remove(position);
        mLayoutId++;
//        if (mNotAddFriends.size() == 0) {
//            mAddMemberIcon.setVisibility(View.GONE);
//        }

        refreshPayerSpinner();
        changeToEvenSplitType();
    }

    private void refreshPayerSpinner() {
        mNames = new ArrayList<>();
        if (mAddedFriends.size() != 0) {
            mNames.add("你");
            for (Friend friend : mAddedFriends) {
                mNames.add(friend.getName());
            }
        }
        mPayerSpinner.setAdapter(new ArrayAdapter<>(mContext, R.layout.dropdown_style, mNames));
        mPayerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mWhoPays = mNames.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mWhoPays = mNames.get(0);
            }
        });

    }

    @Override
    public void showCurrentDate(String date) {
        mPickDate.setText(date);
    }

    @Override
    public void setGroupList(List<Group> groupList) {
        mGroup = new ArrayList<>(groupList);
        List<String> groupNames = new ArrayList<>();
        groupNames.add("無");
        for (Group group : mGroup) {
            groupNames.add(group.getName());
        }
        mGroupSpinner.setAdapter(new ArrayAdapter<>(mContext, R.layout.dropdown_style, groupNames));
        mGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.selectGroup(position);
                mAddMemberLayout.removeViews(2, mAddedFriends.size());
                if (position != 0) {
                    mAddedFriends = new ArrayList<>();
                    mNotAddFriends = new ArrayList<>();
                    List<String> memberIdList = new ArrayList<>(mGroup.get(position - 1).getMembers());
                    List<Friend> friends = FriendList.getInstance().getFriendList();
                    for (String memberid : memberIdList) {
                        for (int i = 0; i < friends.size(); i++) {
                            if (friends.get(i).getUid().equals(memberid)) {
                                mNotAddFriends.add(friends.get(i));
                            }
                        }

                    }
                    Collections.sort(mNotAddFriends, new Comparator<Friend>() {
                        @Override
                        public int compare(Friend o1, Friend o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                } else {
                    mAddedFriends = new ArrayList<>();
                    mNotAddFriends = new ArrayList<>(FriendList.getInstance().getFriendList());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPresenter.selectGroup(0);
            }
        });
    }

    private int parseInt(String s) {
        if (s.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(s);
        }
    }


    private View.OnClickListener mAddMemberClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mItemNameLayout = (ConstraintLayout) LayoutInflater.from(mContext).inflate(R.layout.item_member_name, mAddMemberLayout, false);
            mFriendName = mItemNameLayout.findViewById(R.id.add_member_user_name);
            mFriendImage = mItemNameLayout.findViewById(R.id.add_member_user_image);

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_list_member, null, false);

            RecyclerView recyclerView = dialogView.findViewById(R.id.dialog_add_member_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mMemberAdapter = new AddSplitMemberAdapter(mPresenter, mNotAddFriends);
            recyclerView.setAdapter(mMemberAdapter);

            mFriendDialog = new AlertDialog.Builder(getContext())
                    .setView(dialogView)
                    .show();

            mFriendDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        }
    };

    private AdapterView.OnItemSelectedListener mItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mPresenter.selectSplitType(position);
            switch (position) {
                default:
                case 0:
                    changeToEvenSplitType();
                    break;
                case 1:
                    mMoney = mTotalMoney.getText().toString();
                    if (!mMoney.equals("") && !mMoney.equals("0") && mAddedFriends.size() != 0) {
                        mDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_partial, null);

                        RecyclerView recyclerView = mDialogView.findViewById(R.id.dialog_recycler_view);
                        mPartialAdapter = new SplitPartialAdapter(mMoney, mAddedFriends, mPresenter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(mPartialAdapter);

                        mDialogPartial = new AlertDialog.Builder(getContext())
                                .setView(mDialogView)
                                .setCancelable(false)
                                .show();
                        mDialogPartial.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        mDialogPartial.getWindow().setBackgroundDrawableResource(R.color.transparent);

                        TextView correct = mDialogView.findViewById(R.id.dialog_correct_text);
                        TextView cancel = mDialogView.findViewById(R.id.dialog_cancel_text);

                        correct.setOnClickListener(AddListFragment.this);
                        cancel.setOnClickListener(AddListFragment.this);
                    } else if (mAddedFriends.size() == 0) {
                        Toast.makeText(getContext(), "至少需一個朋友參與拆帳", Toast.LENGTH_SHORT).show();
                        changeToEvenSplitType();
                    } else {
                        Toast.makeText(getContext(), "金額不可為 0 ", Toast.LENGTH_SHORT).show();
                        changeToEvenSplitType();
                    }
                    break;
                case 2:
                    mMoney = mTotalMoney.getText().toString();
                    if (!mMoney.equals("") && !mMoney.equals("0") && mAddedFriends.size() != 0) {
                        mDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_partial, null);

                        RecyclerView recyclerView = mDialogView.findViewById(R.id.dialog_recycler_view);
                        mPercentAdapter = new SplitPercentAdapter(mMoney, mAddedFriends, mPresenter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(mPercentAdapter);

                        mDialogPartial = new AlertDialog.Builder(getContext())
                                .setView(mDialogView)
                                .setCancelable(false)
                                .show();
                        mDialogPartial.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        mDialogPartial.getWindow().setBackgroundDrawableResource(R.color.transparent);

                        TextView correct = mDialogView.findViewById(R.id.dialog_correct_text);
                        TextView cancel = mDialogView.findViewById(R.id.dialog_cancel_text);

                        correct.setOnClickListener(AddListFragment.this);
                        cancel.setOnClickListener(AddListFragment.this);
                    } else if (mAddedFriends.size() == 0) {
                        Toast.makeText(getContext(), "至少需一個朋友參與拆帳", Toast.LENGTH_SHORT).show();
                        changeToEvenSplitType();

                    } else {
                        Toast.makeText(getContext(), "金額不可為 0 ", Toast.LENGTH_SHORT).show();
                        changeToEvenSplitType();
                    }
                    break;
                case 3:
                    mMoney = mTotalMoney.getText().toString();
                    if (!mMoney.equals("") && !mMoney.equals("0") && mAddedFriends.size() != 0) {
                        mDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_partial, null);

                        RecyclerView recyclerView = mDialogView.findViewById(R.id.dialog_recycler_view);
                        mFreeAdapter = new SplitFreeAdapter(mMoney, mAddedFriends, mPresenter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(mFreeAdapter);

                        mDialogPartial = new AlertDialog.Builder(getContext())
                                .setView(mDialogView)
                                .setCancelable(false)
                                .show();
                        mDialogPartial.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        mDialogPartial.getWindow().setBackgroundDrawableResource(R.color.transparent);

                        TextView correct = mDialogView.findViewById(R.id.dialog_correct_text);
                        TextView cancel = mDialogView.findViewById(R.id.dialog_cancel_text);

                        correct.setOnClickListener(AddListFragment.this);
                        cancel.setOnClickListener(AddListFragment.this);
                    } else if (mAddedFriends.size() == 0) {
                        Toast.makeText(getContext(), "至少需一個朋友參與拆帳", Toast.LENGTH_SHORT).show();
                        changeToEvenSplitType();

                    } else {
                        Toast.makeText(getContext(), "金額不可為 0 ", Toast.LENGTH_SHORT).show();
                        changeToEvenSplitType();
                    }
                    break;

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mPresenter.selectSplitType(0);
        }
    };
}
