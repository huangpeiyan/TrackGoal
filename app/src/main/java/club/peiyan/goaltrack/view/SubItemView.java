package club.peiyan.goaltrack.view;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import club.peiyan.goaltrack.MainActivity;
import club.peiyan.goaltrack.R;
import club.peiyan.goaltrack.data.DBHelper;
import club.peiyan.goaltrack.data.GoalBean;
import club.peiyan.goaltrack.data.ScoreBean;
import club.peiyan.goaltrack.plan.DialogFragmentCreatePlan;
import club.peiyan.goaltrack.utils.CalendaUtils;
import club.peiyan.goaltrack.utils.ToastUtil;

import static android.view.View.inflate;

/**
 * Created by HPY.
 * Time: 2018/7/21.
 * Desc:
 */

public class SubItemView implements View.OnClickListener {

    private final MainActivity mActivity;
    private final DBHelper mDBHelper;
    private final View mRootView;

    public SubItemView(MainActivity mMainActivity, String item,
                       GoalsAdapter mAdapter, GoalBean mBean) {
        mActivity = mMainActivity;
        mDBHelper = mActivity.getDBHelper();

        mRootView = inflate(mActivity, R.layout.sub_item_check, null);
        TextView tvItem = mRootView.findViewById(R.id.tvItem);
        SeekBar sb = mRootView.findViewById(R.id.sbProgress);

        final ScoreBean mScoreBean = mDBHelper.getScoreByTitleDate(item, CalendaUtils.getCurrntDate());
        if (mScoreBean != null) {
            sb.setProgress(mScoreBean.getScore());
        }
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int mProgress = seekBar.getProgress();
                ScoreBean mScoreByTitle = mDBHelper.getScoreByTitle(item);
                boolean isSuccess;
                if (mScoreByTitle == null) {
                    isSuccess = mDBHelper.insertScore(-1, "", item, CalendaUtils.getCurrntDate(), mProgress, System.currentTimeMillis());
                } else {
                    isSuccess = mDBHelper.updateScore(mScoreByTitle.getId(), -1, "", item, CalendaUtils.getCurrntDate(), mProgress, System.currentTimeMillis());
                }
                if (isSuccess) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        tvItem.setText(item);
        tvItem.setOnClickListener(this);
        tvItem.setTag(R.id.sub_title, item);
        tvItem.setTag(R.id.parent_title, mBean.getTitle());
        tvItem.setTag(R.id.level, mBean.getLevel());
        tvItem.setTag(R.id.startDate, mBean.getStart());
        tvItem.setTag(R.id.overDate, mBean.getOver());
    }

    public View getRootView() {
        return mRootView;
    }

    @Override
    public void onClick(View v) {
        String subTitle = (String) v.getTag(R.id.sub_title);
        String parentTitle = (String) v.getTag(R.id.parent_title);
        int level = (int) v.getTag(R.id.level);
        String start = (String) v.getTag(R.id.startDate);
        String over = (String) v.getTag(R.id.overDate);
        if (level == 4) {
            ToastUtil.toast("计划太细了，适可而止");
            return;
        }
        GoalBean mGoalBean = mDBHelper.getGoalByTitle(subTitle, parentTitle);
        if (mGoalBean != null) {
            DialogFragmentCreatePlan.showDialog(mActivity.getFragmentManager(), mGoalBean);
        } else {
            DialogFragmentCreatePlan.showDialog(mActivity.getFragmentManager(), subTitle, parentTitle, level + 1, start, over);
        }
    }
}