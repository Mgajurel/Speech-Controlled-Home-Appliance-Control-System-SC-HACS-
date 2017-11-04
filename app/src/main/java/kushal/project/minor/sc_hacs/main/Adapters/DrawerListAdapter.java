package kushal.project.minor.sc_hacs.main.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.Collections;
import java.util.List;

import kushal.project.minor.sc_hacs.R;
import kushal.project.minor.sc_hacs.main.classes.DrawerDataItem;

/**
 * Created by kushal on 8/18/2016.
 */
public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.DrawerViewHolder> {

    private LayoutInflater infalter;
    private Context context;
    List<DrawerDataItem> data = Collections.emptyList();
    private ClickListener clickListener;

    public DrawerListAdapter(Context context, List<DrawerDataItem> data) {

        infalter = LayoutInflater.from(context);
        this.context  = context;
        this.data = data;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =  infalter.inflate(R.layout.drawer_row_layout,parent,false);
        DrawerViewHolder ViewHolder = new DrawerViewHolder(view);

        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {

        DrawerDataItem current = data.get(position);

        holder.icon.setImageResource(current.getIconID());
        holder.Title.setText(current.getItem());

    }

    public void setClickListener(DrawerListAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    protected class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView Title;
        ImageView icon;
        MaterialRippleLayout rip;
        public DrawerViewHolder(View itemView) {
            super(itemView);



            icon = (ImageView) itemView.findViewById(R.id.Drawer_image);
            Title = (TextView) itemView.findViewById(R.id.Drawer_title);
            rip  = (MaterialRippleLayout) itemView.findViewById(R.id.Drawer_ripple);

            rip.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {


            if(clickListener != null){

                clickListener.itemClicked(view, getAdapterPosition());
            }

        }
    }

    public interface ClickListener {

        public void itemClicked(View view,int position);
    }
}


