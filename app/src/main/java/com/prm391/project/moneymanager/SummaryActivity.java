package com.prm391.project.moneymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class SummaryActivity extends AppCompatActivity {
   final Context context = this;
    SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        sqLiteDatabase = openOrCreateDatabase("ExpenseTracker", MODE_PRIVATE, null);
        Calendar c1 = Calendar.getInstance();
        final int month = c1.get(Calendar.MONTH);
        final int year =  c1.get(Calendar.YEAR);
        final String month_name = new DateFormatSymbols().getMonths()[month];
        TextView c_budget = (TextView) findViewById(R.id.c_budget);
        TextView a_budget = (TextView) findViewById(R.id.a_budget);
        TextView expenses = (TextView) findViewById(R.id.expenses);
        TextView liabilities = (TextView) findViewById(R.id.liabilities);
        TextView c_savings = (TextView) findViewById(R.id.c_savings);
        TextView t_savings = (TextView) findViewById(R.id.t_savings);

        //BUDGET
        Cursor c = sqLiteDatabase.rawQuery("select b_amount,b_cash from budget where b_month="+month+" and b_year="+year+";",null);
        if(c.moveToFirst()) {
            int amt = c.getInt(0);
            c_budget.setText(String.valueOf(amt) +" VND");
            int cash = c.getInt(1);
            a_budget.setText(String.valueOf(cash) +" VND");
        }
        c = sqLiteDatabase.rawQuery("select sum(e_amount) from expenses where e_mark=1 and budget_id = (select b_id from budget where b_month=" + month + " and b_year=" + year +")", null);
        //PAID
        if(c.moveToFirst()){
           int sum = c.getInt(0);
            expenses.setText(String.valueOf(sum) +" VND");
        }

        c = sqLiteDatabase.rawQuery("select sum(e_amount) from expenses where e_mark=0 and budget_id = (select b_id from budget where b_month=" + month + " and b_year=" + year + ")",null);
        //PAID
        if(c.moveToFirst()){
            int sum = c.getInt(0);
            liabilities.setText(String.valueOf(sum) +" VND");
        }

        //LIABILITY
        c = sqLiteDatabase.rawQuery("select e_category_id,e_amount from expenses where e_mark=0 and budget_id = (select b_id from budget where b_month=" + month + " and b_year=" + year + ");", null);
        if(c.moveToFirst()){
            do{
                int y=c.getInt(0);
                int amount= c.getInt(1);
                Cursor c2= sqLiteDatabase.rawQuery("select c_name from category where c_id=" + y + ";", null);
                c2.moveToFirst();
                String y1 = c2.getString(0);
            }while(c.moveToNext());
        }
        //SAVINGS
        c = sqLiteDatabase.rawQuery("select s_target from savings where budget_id = (select b_id from budget where b_month = " + month + " and b_year = " + year + ");", null);
        if(c.moveToFirst()){
            int target= c.getInt(0);
            c = sqLiteDatabase.rawQuery("select b_amount,b_cash from budget where b_month =" + month + " and b_year = " + year + ";", null);
            c.moveToFirst();
            int budget = c.getInt(0);
            int cash = c.getInt(1);
            int savings = budget - cash;
            c_savings.setText(String.valueOf(savings) +" VND");
            t_savings.setText(String.valueOf(target) +" VND");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Exit");
            builder.setMessage("Are you sure you want to exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
