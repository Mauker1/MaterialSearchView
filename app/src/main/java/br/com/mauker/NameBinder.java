package br.com.mauker;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

import br.com.mauker.materialsearchview.app.R;
import butterknife.Bind;
import io.c0nnector.github.least.BaseViewHolder;
import io.c0nnector.github.least.Binder;

public class NameBinder extends Binder<NameBinder.NameView, ItemName>{

    public NameBinder(Context context, Class<ItemName> itemNameClass, Class<NameView> cls, @LayoutRes int layoutId) {
        super(context, itemNameClass, cls, layoutId);
    }

    @Override
    public void onBindViewHolder(NameView nameView, ItemName itemName, int i) {
        nameView.textView.setText(itemName.getName());
    }

    public static class NameView extends BaseViewHolder {

        @Bind(R.id.textView)
        TextView textView;

        public NameView(View itemView) {
            super(itemView);
        }
    }

    public static NameBinder instance(Context context){
        return new NameBinder(context, ItemName.class, NameView.class, R.layout.list_name);
    }

}
