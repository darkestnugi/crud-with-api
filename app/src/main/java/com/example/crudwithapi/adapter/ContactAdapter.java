package com.example.crudwithapi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.crudwithapi.ContactDetailActivity;
import com.example.crudwithapi.R;
import com.example.crudwithapi.model.contact;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ContactAdapter extends ArrayAdapter<contact> {

    private Context context;
    private List<contact> employees;

    public ContactAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<contact> objects) {
        super(context, resource, objects);
        this.context = context;
        this.employees = objects;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_contact, parent, false);

        TextView txtcontactId = (TextView) rowView.findViewById(R.id.txtCViewId);
        TextView txtcontactName = (TextView) rowView.findViewById(R.id.txtCViewName);
        TextView txtcontactEmail = (TextView) rowView.findViewById(R.id.txtCViewEmail);
        TextView txtcontactPhone = (TextView) rowView.findViewById(R.id.txtCViewPhone);

        txtcontactId.setText(String.format("id: %s", employees.get(pos).getID()));
        txtcontactName.setText(String.format("name: %s", employees.get(pos).getName()));
        txtcontactEmail.setText(String.format("email: %s", employees.get(pos).getEmail()));
        txtcontactPhone.setText(String.format("phone: %s", employees.get(pos).getPhone()));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start Activity employee Form
                Intent intent = new Intent(context, ContactDetailActivity.class);
                intent.putExtra("contact_id", String.valueOf(employees.get(pos).getID()));
                intent.putExtra("contact_name", employees.get(pos).getName());
                intent.putExtra("contact_email", employees.get(pos).getEmail());
                intent.putExtra("contact_phone", employees.get(pos).getPhone());
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}