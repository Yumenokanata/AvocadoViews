package indi.yume.view.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import indi.yume.tools.adapter_renderer.ContextAware;
import indi.yume.tools.adapter_renderer.recycler.BaseRenderer;

/**
 * Created by yume on 16-5-27.
 */

public class TestRecyclerRenderer extends BaseRenderer<String> implements ContextAware {
    @Bind(R.id.company_name_textview)
    TextView companyNameTv;
    @Bind(R.id.item_keep_btn)
    ImageView keepBtn;
    Context context;
    @Bind(R.id.business_detail_textview)
    TextView businessDetailTextview;
    @Bind(R.id.location_textview)
    TextView locationTextview;
    @Bind(R.id.income_textiview)
    TextView incomeTextiview;
    @Bind(R.id.company_profile_textview)
    TextView companyProfileTextview;

    @Override
    public void render() {
        final String jobData = getContent();

        companyNameTv.setText(jobData);
        companyProfileTextview.setText(jobData);
        businessDetailTextview.setText(jobData);
        locationTextview.setText(jobData);
        incomeTextiview.setText(jobData);
    }

    @Override
    protected View inflate(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.search_result_listview_item_type1, viewGroup, false);
    }

    @Override
    protected void findView(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected void setListener(View view) {

    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}
