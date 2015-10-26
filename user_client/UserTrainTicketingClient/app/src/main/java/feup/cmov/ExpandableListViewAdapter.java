package feup.cmov;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Hugo on 20/10/2015.
 */
public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private static final class ViewHolder {
        TextView textLabel;
    }

    private final ArrayList<Route> itemList;
    private final LayoutInflater inflater;
    private Context context;

    public ExpandableListViewAdapter(Context context, ArrayList<Route> itemList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    @Override
    public StationTime getChild(int groupPosition, int childPosition) {

        return itemList.get(groupPosition).stationTimes.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemList.get(groupPosition).stationTimes.size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             final ViewGroup parent) {
        View resultView = convertView;
        ViewHolder holder;


        if (resultView == null) {

            resultView = inflater.inflate(R.layout.expandable_list_row, null); //TODO change layout id
            holder = new ViewHolder();
            holder.textLabel = (TextView) resultView.findViewById(R.id.grp_child); //TODO change view id
            resultView.setTag(holder);
        } else {
            holder = (ViewHolder) resultView.getTag();
        }

        final StationTime item = getChild(groupPosition, childPosition);

        holder.textLabel.setText(item.toString());

        return resultView;
    }

    @Override
    public Route getGroup(int groupPosition) {
        return itemList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return itemList.size();
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View theConvertView, ViewGroup parent) {
        View resultView = theConvertView;
        ViewHolder holder;

        if (resultView == null) {
            resultView = inflater.inflate(R.layout.expandable_list_group, null); //TODO change layout id
            holder = new ViewHolder();
            holder.textLabel = (TextView) resultView.findViewById(R.id.row_name); //TODO change view id
            Button buyButton = (Button) resultView.findViewById(R.id.buy_button);
            buyButton.setFocusable(false);


            buyButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {

                    //if not logged in, make user login first and redirect to the purchase activity
                    //else redirect to the purchase activity
                    SharedPreferences spToken = context.getSharedPreferences("login", 0);

                    String token = spToken.getString("token", null);

                    View parentRow = (View) v.getParent();
                    ExpandableListView listView = (ExpandableListView) parentRow.getParent();
                    final int position = listView.getPositionForView(parentRow);
                    View rootView = (View) listView.getParent();
                    TextView dateView = (TextView)rootView.findViewById(R.id.route_date);
                    String date = dateView.getText().toString();

                    Route r = (Route)listView.getItemAtPosition(position);

                    if(token == null){
                        //user not logged in
                        Intent loginActivity = new Intent(context, LoginActivity.class);
                        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        //add shared preference with the ticket to be redirected after the login (verify after successful login)
                        SharedPreferences spRoute = context.getSharedPreferences("route", 0);
                        SharedPreferences.Editor editor = spRoute.edit();
                        editor.putString("route", "true");
                        editor.putString("route_from", r.stationTimes.get(0).station.toString());
                        editor.putString("route_to", r.stationTimes.get(r.stationTimes.size() - 1).station.toString());
                        editor.putString("route_time", r.stationTimes.get(0).time);
                        editor.putString("route_date", date);
                        editor.commit();

                        context.startActivity(loginActivity);
                    }
                    else{
                        //user logged in

                        ApiRequest request = new ApiRequest(context, TicketPurchaseActivity.class, null);
                        request.execute("route?from=" + r.stationTimes.get(0).station + "&to=" + r.stationTimes.get(r.stationTimes.size() - 1).station + "&time=" + r.stationTimes.get(0).time + "&date=" + date);
                    }
                }
            });


            resultView.setTag(holder);
        } else {
            holder = (ViewHolder) resultView.getTag();
        }

        final Route item = getGroup(groupPosition);

        holder.textLabel.setText(item.toString());

        return resultView;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
