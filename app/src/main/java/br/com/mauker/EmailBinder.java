package br.com.mauker;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

import br.com.mauker.materialsearchview.app.R;
import butterknife.Bind;
import io.c0nnector.github.least.BaseViewHolder;
import io.c0nnector.github.least.Binder;

public class EmailBinder extends Binder<EmailBinder.EmailView, ItemEmail>{


    public EmailBinder(Context context, Class<ItemEmail> itemEmailClass, Class<EmailView> cls, @LayoutRes int layoutId) {
        super(context, itemEmailClass, cls, layoutId);
    }

    @Override
    public void onBindViewHolder(EmailView emailView, ItemEmail itemEmail, int i) {
        emailView.textView.setText(itemEmail.getEmail());
    }

    public static class EmailView extends BaseViewHolder {

        @Bind(R.id.textView)
        TextView textView;

        public EmailView(View itemView) {
            super(itemView);
        }
    }

    public static EmailBinder instance(Context context){
        return new EmailBinder(context, ItemEmail.class, EmailView.class, R.layout.list_email);
    }
}
