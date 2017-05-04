package example.com.sunshine.download.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import java.lang.ref.WeakReference;

import example.com.sunshine.R;
import example.com.sunshine.download.Application.TLiveApplication;
import example.com.sunshine.download.Home.Main1Activity;
import example.com.sunshine.download.Utils.ToastUtil;
import example.com.sunshine.download.injection.component.ActivityComponent;
import example.com.sunshine.download.injection.component.DaggerActivityComponent;
import example.com.sunshine.download.injection.module.ActivityModule;
import example.com.sunshine.download.loading.BaseView;
import example.com.sunshine.download.loading.VaryViewHelperController;


public abstract class BaseFragment extends Fragment implements View.OnClickListener,BaseView {


    protected VaryViewHelperController mVaryViewHelperController;

    protected abstract void initView(Bundle savedInstanceState, View rootView);

    //获取布局文件ID  
    protected abstract int getLayoutId();

    protected abstract void initData();

    protected Context mContext;
    private WeakReference<Context> mWeakReference;

    private View view;

    protected long lastClick = 0;

    /**
     * View点击
     **/
    protected abstract void OnClick(View v);


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mWeakReference = new WeakReference<>(context);
        mContext = mWeakReference.get();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getLayoutId() > 0) {

            view = inflater.inflate(getLayoutId(), container, false);
            initView(savedInstanceState, view);
            if (null != getLoadingTargetView()) {
                mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
            }
            initData();
            return view;
        }

        return null;
    }

    //添加fragment
    protected void addFragment(int fragment_full,BaseFragment fragment) {
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    /*.setCustomAnimations(
                    R.anim.slide_out_bottom,
                    R.anim.slide_in_bottom)*/
                    .replace(fragment_full, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }
    //移除fragment
    protected void removeFragment() {
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            getActivity().finish();
        }
    }

            @Override
    public void onResume() {
        super.onResume();

    }

    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void toast(String msg) {
        ToastUtil.showToast(getActivity(), msg);
    }

    protected void toast(int id) {
        toast(getActivity().getResources().getString(id));
    }

    @SuppressWarnings("unchecked")
    public final <T extends View> T findById(int id) {
        try {
            return (T) view.findViewById(id);
        } catch (ClassCastException ex) {
            throw ex;
        }
    }

    @SuppressWarnings("unchecked")
    public final <T extends View> T findById(int id, View view) {
        try {
            return (T) view.findViewById(id);
        } catch (ClassCastException ex) {
            throw ex;
        }
    }

    /**
     * [防止快速点击]
     *
     * @return
     */
    private boolean fastClick() {

        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (fastClick())
            OnClick(v);
    }
//    private void setTransition(Intent intent) {
//        if (Build.VERSION.SDK_INT >= 21) {
//            getActivity().getWindow().setExitTransition(new Explode());
//        }
//        Bundle bundle = ActivityOptionsCompat
//                .makeSceneTransitionAnimation(getActivity())
//                .toBundle();
//        if (Build.VERSION.SDK_INT >= 21) {
//            startActivity(intent, bundle);
//        } else {
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
//    }
            /**
             * 跳转到指定的Activity
             *
             * @param targetActivity 要跳转的目标Activity
             */
    protected final void startActivity(@NonNull Class<?> targetActivity) {

        Intent intent = new Intent(getActivity(), targetActivity);
        startActivity(intent);
    }

    /**
     * 跳转到指定的Activity
     *
     * @param flags          intent flags
     * @param targetActivity 要跳转的目标Activity
     */
    protected final void startActivity(int flags, @NonNull Class<?> targetActivity) {
        final Intent intent = new Intent(getActivity(), targetActivity);
        intent.setFlags(flags);
        startActivity(intent);
    }

    /**
     * 跳转到指定的Activity
     *
     * @param data           Activity之间传递数据，Intent的Extra key为Constant.EXTRA_NAME.DATA
     * @param targetActivity 要跳转的目标Activity
     */
    protected final void startActivity(@NonNull Bundle data, @NonNull Class<?> targetActivity) {
        final Intent intent = new Intent();
        if (data != null) {
            intent.putExtras(data);
            intent.setClass(getActivity(), targetActivity);
            startActivity(intent);
        }
    }

    /**
     * 跳转到指定的Activity
     *
     * @param extraName      要传递的值的键名称
     * @param extraValue     要传递的String类型值
     * @param targetActivity 要跳转的目标Activity
     */
    public final void startActivity(@NonNull String extraName, @NonNull String extraValue, @NonNull Class<?> targetActivity) {
        if (!TextUtils.isEmpty(extraName)) {
            final Intent intent = new Intent(getActivity(), targetActivity);
            intent.putExtra(extraName, extraValue);
            startActivity(intent);
        } else {
            throw new NullPointerException("传递的值的键名称为null或空");
        }


    }

    public final void startActivityForResult(int action, @NonNull Class<?> targetActivity) {
        Intent mIntent = new Intent(getActivity(), targetActivity);
        startActivityForResult(mIntent, action);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }


    @Override
    public void showLoading() {
        if (mVaryViewHelperController != null) {
            mVaryViewHelperController.showLoading();
        }
    }

    @Override
    public void showEmptyView(String msg) {
        if (mVaryViewHelperController != null) {
            mVaryViewHelperController.showEmpty(msg);
        }
    }

    @Override
    public void showNetError() {
        if (mVaryViewHelperController != null) {
            mVaryViewHelperController.showNetworkError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reLoadData();
                }
            });
        }
    }

    /**
     * 重新请求数据
     */
    public void reLoadData() {
        showLoading();
    }

    @Override
    public void refreshView() {
        if (mVaryViewHelperController != null) {
            mVaryViewHelperController.restore();
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter){
            return AnimationUtils.loadAnimation(mContext,R.anim.slide_out_bottom);
        }else{
            return  AnimationUtils.loadAnimation(mContext,R.anim.slide_in_bottom);
        }
    }
}