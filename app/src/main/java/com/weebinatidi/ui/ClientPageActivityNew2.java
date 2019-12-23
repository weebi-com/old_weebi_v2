package com.weebinatidi.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.weebinatidi.Config;
import com.weebinatidi.R;
import com.weebinatidi.model.ClientRepository;
import com.weebinatidi.model.InvoiceRepository;
import com.weebinatidi.model.ItemRepository;
import com.weebinatidi.model.OperationClientRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.weebinatidi.Config.formaterSolde;
import static com.weebinatidi.ui.Calculator.isSmart;
import static com.weebinatidi.ui.Calculator.isSmartsmall;
import static com.weebinatidi.ui.Calculator.isTablet;
import static com.weebinatidi.ui.Calculator.isTablet7;

public class ClientPageActivityNew2 extends BaseActivity  {

    public static final String EXTRA_CLIENT_PHONE = "CLIENT_PHONE";
    public static final String EXTRA_CLIENT_ARCHIVE = "CLIENT_ARCHIVE";

    RealmResults<OperationClientRepository> results;
    RealmResults<OperationClientRepository> results1;
    private ListView mDepotList ,mDepotList1;

    private TextView nombrefacture;
    private Realm realm;
    private String mPhone;
    private String solde;
    private String archived;
    private DepositAdapter mAdapter;
    private DepotAdapter depotAdapter;
    private ClientRepository thisClient, thisClient1;
    private OperationClientRepository operationClientRepository;
    private ArrayList<String> lesdates = new ArrayList<>();
    //private ArrayList<Integer>lesprisescon=new ArrayList<>();
    private ArrayList<Integer> lesidfactures = new ArrayList<>();
    private RealmChangeListener reamlListener;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isInvoice = true;
    private int taille = 0;
    private Button depot, valide, edit;
    private TextView textView;
    private boolean isInvoice1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isTablet(this) || isTablet7(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (isSmart(this) || isSmartsmall(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //Toast.makeText(getApplicationContext(), "PORTRAIT", Toast.LENGTH_SHORT).show();
        }


        mPhone = getIntent().getStringExtra(EXTRA_CLIENT_PHONE);
        archived = getIntent().getStringExtra(EXTRA_CLIENT_ARCHIVE);
        realm = Realm.getDefaultInstance();

        /**
         * debut traitement des factures
         * */
        // get the actual client info.
        thisClient = realm.where(ClientRepository.class).equalTo("numero", mPhone).findFirst();
        //get operationclient
        RealmList<OperationClientRepository> op = thisClient.getOperationClients();
        results=op.where().equalTo("isInvoice", isInvoice).findAllSorted("date", Sort.DESCENDING);
        //results = op.where().equalTo("isInvoice", isInvoice).findAll();

        taille = results.size();
        getSupportActionBar().setTitle(thisClient.getNom() + "  " + mPhone);

        mDepotList = (ListView)findViewById(R.id.deposit_liste);
        nombrefacture = (TextView)findViewById(R.id.nbrefacture);
        depot = (Button) findViewById(R.id.client_deposit);
        valide = (Button) findViewById(R.id.prise);
        edit = (Button) findViewById(R.id.edit_client);

        mAdapter = new DepositAdapter(results);
        mDepotList.setAdapter(mAdapter);

        nombrefacture.setText("" + taille);

        depot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClientDeposit();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onEditClient();
                                    }
                                }
        );

        valide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int soldeint = Integer.valueOf(thisClient.getSolde());

                    String lenom = thisClient.getNom();
                    String lenum = thisClient.getNumero();
                    int lesolde = thisClient.getSolde();
                    solde = String.valueOf(lesolde);
                    //Toast.makeText(getActivity(),""+"\n"+lenom+"\n"+lenum+"\n"+solde,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), FacturesSelect.class);

                    intent.putIntegerArrayListExtra("lesidfact", lesidfactures);

                    intent.putExtra("lesolde", solde);
                    startActivity(intent);


            }
        });

       /* mDepotList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // show the invoice details.

                Log.d(TAG, " posiinside " + position);

//                Log.d(TAG," size "+mDepotList.getAdapter().getCount());
//                if(position != 0)
//                {
                //because the header has been added the new size is the list size +1 so we need to remove that 1 to make sure
                // we dont get arayboundexception as if not we going out of the array...
//                    Log.d(TAG,"idfacture"+thisClient.getOperationClients().get(0).getInvoiceRepository().getItems().size());
                if ((thisClient.getOperationClients().get(position).getInvoiceRepository() != null) && (thisClient.getOperationClients().get(position).isInvoice() == true)) {
                    Log.d(TAG, "idfacture" + thisClient.getOperationClients().get(position).getInvoiceRepository().getId());

                    InvoiceRepository invoice = thisClient.getOperationClients().get(position).getInvoiceRepository();
                    Intent intent = new Intent(getApplicationContext(), InvoiceDetailsActivity.class);
//                    Log.d(TAG,"idfacture"+invoice.getItems().size());
                    intent.putExtra(InvoiceDetailsActivity.EXTRA_INVOICE_ID, invoice.getId());
                    intent.putExtra(InvoiceDetailsActivity.EXTRA_ISARCHIVE, false);
                    //Toast.makeText(getApplicationContext(),""+invoice.getId()+"\n"+position,Toast.LENGTH_SHORT).show();
                    //startActivity(intent);
                } else {
//                    InvoiceRepository invoice = thisClient.getOperationClients().get(position).getInvoiceRepository();
//                    Intent intent = new Intent(ClientPageActivity.this, InvoiceDetailsActivity.class);
////                    Log.d(TAG,"idfacture"+invoice.getItems().size());
//                    intent.putExtra(InvoiceDetailsActivity.EXTRA_INVOICE_ID, invoice.getId());
//                    intent.putExtra(InvoiceDetailsActivity.EXTRA_ISARCHIVE,false);
//                    startActivity(intent);
                }

//
//                }

            }
        });*/
        mAdapter.notifyDataSetChanged();

        /*
        * fin traitement des factures
        * et debut traitement des depots
        * */

        // get the actual client info.
        thisClient1 = realm.where(ClientRepository.class).equalTo("numero", mPhone).findFirst();
        //get operationclient

        RealmList<OperationClientRepository> ope = thisClient1.getOperationClients();
        results1 = ope.where().equalTo("isInvoice", isInvoice1).findAll();

        mDepotList1 = (ListView) findViewById(R.id.depot_liste);
        textView = (TextView) findViewById(R.id.solde);
        textView.setText("" + thisClient1.getSolde());
        depotAdapter = new DepotAdapter(results1);
        mDepotList1.setAdapter(depotAdapter);

        mDepotList1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // show the invoice details.

//
                if ((thisClient1.getOperationClients().get(position).getInvoiceRepository() != null) && (thisClient1.getOperationClients().get(position).isInvoice() == false)) {
                  //  Log.d(TAG, "idfacture" + thisClient1.getOperationClients().get(position).getInvoiceRepository().getId());

                    /*InvoiceRepository invoice = thisClient1.getOperationClients().get(position).getInvoiceRepository();
                    Intent intent = new Intent(getApplicationContext(), InvoiceDetailsActivity.class);
//                    Log.d(TAG,"idfacture"+invoice.getItems().size());
                    intent.putExtra(InvoiceDetailsActivity.EXTRA_INVOICE_ID, invoice.getId());
                    intent.putExtra(InvoiceDetailsActivity.EXTRA_ISARCHIVE, false);
                    startActivity(intent);*/
                }

//
//                }

            }
        });
        depotAdapter.notifyDataSetChanged();

        /**
         * fin traitement des depots
         */

    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_client_page_new2;
    }

    private class DepositAdapter extends BaseAdapter {

        private List<OperationClientRepository> mOperationClients = null;

        public DepositAdapter(List<OperationClientRepository> list) {
            mOperationClients = list;
        }

        @Override
        public int getCount() {
            if (mOperationClients != null)
                return mOperationClients.size();
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return mOperationClients.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_deposit, parent, false);
            }

            TextView depotType = (TextView) convertView.findViewById(R.id.deposit);
            //TextView depotDate = (TextView) convertView.findViewById(R.id.deposit_date);
            TextView depotMontant = (TextView) convertView.findViewById(R.id.deposit_amount);
            Button voir = (Button) convertView.findViewById(R.id.voir);
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            final int lid = mOperationClients.get(position).getInvoiceRepository().getId();
            final InvoiceRepository invoiceRepository = mOperationClients.get(position).getInvoiceRepository();

            voir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), InvoiceDetailsActivity.class);
//                    Log.d(TAG,"idfacture"+invoice.getItems().size());
                    intent.putExtra(InvoiceDetailsActivity.EXTRA_INVOICE_ID, lid);
                    intent.putExtra(InvoiceDetailsActivity.EXTRA_ISARCHIVE, false);
                    startActivity(intent);
                }
            });

            int montant = mOperationClients.get(position).getMontant();

            boolean contain = false;
            int valeurATrouver = lid;


            for (int i = 0; i < lesidfactures.size(); i++) {
                if (lesidfactures.get(i) == valeurATrouver) {
                    contain = true;
                }
            }
            if (contain) {
                checkBox.setChecked(true);
                if (checkBox.isChecked()) {
                    //lesprisescon=new ArrayList<>();
                    //Toast.makeText(getActivity(),""+lid,Toast.LENGTH_SHORT).show();
                    lesidfactures.add(lid);
                    int taille = lesidfactures.size();
                }

            } else {
                checkBox.setChecked(false);

            }
            //lors de la click sur le checkbox
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //checkBox.setChecked(true);
                    if (checkBox.isChecked()) {


                        //on ajoute lid de la facture à la liste des factures choisies
                        lesidfactures.add(lid);
                        int taille = lesidfactures.size();

                        //Toast.makeText(getActivity(),""+lesidfactures+"\n taille "+taille,Toast.LENGTH_SHORT).show();
                    } else if (!checkBox.isChecked()) {
                        int taille = lesidfactures.size();

                        Iterator<Integer> iterator = lesidfactures.iterator();
                        while (iterator.hasNext()) {
                            Integer o = iterator.next();
                            if (o == lid) {
                                // On supprime l'élément courant de la liste
                                //Toast.makeText(getActivity(),""+lesidfactures+"\n taille "+taille,Toast.LENGTH_SHORT).show();
                                iterator.remove();
                            }
                        }
                    }

                }


            });

            if (mOperationClients.get(position).getInvoiceRepository() != null) {
                if (mOperationClients.get(position).isInvoice() == true) {
                    /*convertView.setBackgroundColor(getResources().getColor(R.color.lightgray));
                    depotType.setTextColor(Color.WHITE);
                    depotMontant.setTextColor(Color.WHITE);
                    depotType.setText(" "+mOperationClients.get(position).getInvoiceRepository().getItems().get(0).getName());*/
                    String lemontant = formaterSolde(montant);
                    depotMontant.setText(getString(R.string.montant_egale) + String.valueOf(lemontant));
                    depotType.setText(getString(R.string.facture_of)
                            + Config.getFormattedDate(mOperationClients.get(position).getDate()));
                }
            }

            return convertView;
        }
    }

    private class DepotAdapter extends BaseAdapter {

        private List<OperationClientRepository> mOperationClients = null;

        public DepotAdapter(List<OperationClientRepository> list) {
            mOperationClients = list;
        }

        @Override
        public int getCount() {
            if (mOperationClients != null)
                return mOperationClients.size();
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return mOperationClients.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_depot, parent, false);
            }

            TextView depotType = (TextView) convertView.findViewById(R.id.deposit);
            //TextView depotDate = (TextView) convertView.findViewById(R.id.deposit_date);
            TextView depotMontant = (TextView) convertView.findViewById(R.id.deposit_amount);
            Button voirdep = (Button) convertView.findViewById(R.id.voirdep);
            final int lid = mOperationClients.get(position).getInvoiceRepository().getId();
            final InvoiceRepository invoiceRepository = mOperationClients.get(position).getInvoiceRepository();

            voirdep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), InvoiceDetailsActivity.class);
//                    Log.d(TAG,"idfacture"+invoice.getItems().size());
                    intent.putExtra(InvoiceDetailsActivity.EXTRA_INVOICE_ID, lid);
                    intent.putExtra(InvoiceDetailsActivity.EXTRA_ISARCHIVE, false);
                    startActivity(intent);
                }
            });

            int montant = mOperationClients.get(position).getMontant();

            String name = mOperationClients.get(position).getInvoiceRepository().getItems().get(0).getName();
            String lemontant = formaterSolde(montant);
            depotMontant.setText(getString(R.string.montant_egale) + String.valueOf(lemontant));
            depotType.setText(" " + mOperationClients.get(position).getInvoiceRepository().getItems().get(0).getName());
            Config.getFormattedDate(mOperationClients.get(position).getDate());


//
            return convertView;
        }
    }

    private void onEditClient() {


        final AlertDialog RegisterClientDialog;
        // launch a dialog to register a client.
//        RegisterClientbuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        AlertDialog.Builder RegisterClientbuilder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_add_new_client, null, false);
        RegisterClientbuilder.setView(customView);
        //TODO Should be used where its asked to select a client to do something..

//        RegisterClientbuilder.setSingleChoiceItems()

        final FormEditText username = (FormEditText) customView.findViewById(R.id.client_name);
        final FormEditText phone = (FormEditText) customView.findViewById(R.id.client_number);
        //final FormEditText phone2 = (FormEditText) customView.findViewById(R.id.client_number2);
        final FormEditText solde = (FormEditText) customView.findViewById(R.id.client_solde);
        final FormEditText email = (FormEditText) customView.findViewById(R.id.client_mail);
        email.setVisibility(View.GONE);
        solde.setVisibility(View.GONE);


//look for the shop in the database...
        RealmQuery<ClientRepository> query = realm.where(ClientRepository.class).equalTo("numero", thisClient.getNumero());
        RealmResults<ClientRepository> size = query.findAll();
        Log.d(" taille ", " " + size.size());
        ClientRepository tmp = query.findFirst();


        if (tmp != null) {
            username.setText(tmp.getNom());
            phone.setText(tmp.getNumero());
            //phone.setText(tmp.getNumero());
//            email.setText(tmp.getMail());

        }


        RegisterClientbuilder.setCancelable(true);

//        RegisterClientbuilder.setInverseBackgroundForced(true);
        RegisterClientbuilder.setTitle(getString(R.string.edit_client));
        RegisterClientDialog = RegisterClientbuilder.create();

        RegisterClientDialog.show();
        final ImageView okbtn = (ImageView) customView.findViewById(R.id.oknewclient);


        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProcessEditclient(username, phone, phone, email, solde, RegisterClientDialog);
                /*Intent intent=new Intent(getApplicationContext(),ClientPageActivity.class);
                intent.putExtra(ClientPageActivity.EXTRA_CLIENT_PHONE, ""+phone);
                intent.putExtra(ClientPageActivity.EXTRA_CLIENT_ARCHIVE, "nonarchived");
                startActivity(intent);
                finish();*/
            }
        });


    }

    public void ProcessEditclient(FormEditText username, FormEditText phone, FormEditText phone2, FormEditText email, FormEditText solde, AlertDialog alertDialog) {
//        FormEditText[] allFields = {username, phone,phone2,email};
        FormEditText[] allFields = {username, phone, phone};
        boolean allvalid = true;

        for (FormEditText field : allFields) {
            allvalid = field.testValidity() && allvalid;
        }

//                if (isEntryValid(username, phone,phone2))
        if (allvalid) {
            if (phone.getText().toString().equals(phone.getText().toString())) {
                if (!thisClient.getNumero().equals(phone.getText().toString())) {
                    RealmQuery<ClientRepository> query = realm.where(ClientRepository.class);
                    ClientRepository tmp = query.equalTo("numero", phone.getText().toString()).findFirst();

                    if (tmp == null) {
                        realm.beginTransaction();
                        thisClient.setMail(email.getText().toString());
                        thisClient.setNom(username.getText().toString());
                        thisClient.setNumero(phone.getText().toString());
                        thisClient.setMail(email.getText().toString());

                        realm.copyToRealmOrUpdate(thisClient);
                        realm.commitTransaction();

                        Snackbar.make(phone, getString(R.string.edited_client_ok), Snackbar.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        // check the validity of the entry
                        // register the user in the database
                        // send confirmation message to the operator
                    } else {
                        phone2.setError(getString(R.string.numalreadyused), getResources().getDrawable(R.drawable.add_user));
                        Snackbar.make(phone, getString(R.string.numalreadyused), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    realm.beginTransaction();
                    thisClient.setMail(email.getText().toString());
                    thisClient.setNom(username.getText().toString());
                    thisClient.setNumero(phone.getText().toString());
                    thisClient.setMail(email.getText().toString());

                    realm.copyToRealmOrUpdate(thisClient);
                    realm.commitTransaction();

                    Snackbar.make(phone, getString(R.string.edited_client_ok), Snackbar.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }

            } else {
//                        phone.setError("les numeros ne correspondent pas",getResources().getDrawable(R.drawable.add_user));
                phone2.setError(getString(R.string.numnomatch), getResources().getDrawable(R.drawable.add_user));
                Snackbar.make(phone, getString(R.string.numnomatch), Snackbar.LENGTH_LONG).show();

            }
        }

    }

    private void onClientDeposit() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_add_new_deposit, null, false);
//        final Spinner clientSpinner = (Spinner) customView.findViewById(R.id.select_client);
//        ImageButton addClient = (ImageButton) customView.findViewById(R.id.add_client);
        final EditText clientSolde = (EditText) customView.findViewById(R.id.client_solde);
        final EditText clientSoldee = (EditText) customView.findViewById(R.id.client_solderewrite);
        final ImageView okbtn = (ImageView) customView.findViewById(R.id.okdepositclient);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(clientSolde, InputMethodManager.SHOW_IMPLICIT);
        // get all the clients


        builder.setView(customView);
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.registerdepot));
        builder.setMessage(getString(R.string.client_deposit));

        final AlertDialog depositDialog = builder.create();


        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientSolde.setError(null);
                clientSoldee.setError(null);
                ClientRepository selectedClient = thisClient;
                // validate the solde
                String solde = clientSolde.getText().toString();
                String soldee = clientSoldee.getText().toString();

                InvoiceRepository invoiceRepository = null;
                if (!TextUtils.isEmpty(solde)) {
                    if (selectedClient != null) {

                        if (solde.equals(soldee)) {
                            realm.beginTransaction();
                            int invoiceidt = ProcessToClientDeposit(realm, selectedClient, solde);

                            invoiceRepository = realm.where(InvoiceRepository.class).equalTo("id", invoiceidt).findFirst();

                            realm.commitTransaction();

                            Snackbar.make(mDepotList, getString(R.string.solderegistersuccess), Snackbar.LENGTH_SHORT).show();
                            depositDialog.dismiss();
                            //finish();
                            Intent intent=new Intent(ClientPageActivityNew2.this, ClientPageActivityNew2.class);
                            intent.putExtra(ClientPageActivityNew.EXTRA_CLIENT_PHONE, mPhone);
                            intent.putExtra(ClientPageActivityNew.EXTRA_CLIENT_ARCHIVE, "nonarchived");
                            //Toast.makeText(getApplicationContext(),""+lenum,Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            clientSoldee.setError("les deux montants ne corresspondent pas");
                        }

                    } else {

                        Snackbar.make(clientSolde, getString(R.string.select_customer), Snackbar.LENGTH_SHORT).show();

                    }
                } else {
                    clientSolde.setError(getString(R.string.emptysolde));
                }
            }
        });
        depositDialog.show();

    }

    public int ProcessToClientDeposit(Realm realms, ClientRepository client, String soldes) {
        // Add the transaction to the deposit
        OperationClientRepository depot = realms.createObject(OperationClientRepository.class);
        depot.setClient(client);
        depot.setMontant(Integer.valueOf(soldes));
        depot.setDate(Calendar.getInstance().getTime());
        depot.setInvoice(false);
        //********************TEMP********************//

        int invoicegoid = Config.getLastInvoiceId(ClientPageActivityNew2.this) + 1;
//                            InvoiceRepository invoicedepot= new InvoiceRepository();
        InvoiceRepository invoicedepot = new InvoiceRepository();
        invoicedepot.setId(invoicegoid);
        Config.setLastInvoiceId(ClientPageActivityNew2.this, invoicegoid);
        invoicedepot.setDate(Calendar.getInstance().getTime());
        invoicedepot.setTotal(Integer.valueOf(soldes));
        invoicedepot.setClient(client);
        invoicedepot.setInvoiceRepo(false);

        int itemgoid = Config.getLastItemId(ClientPageActivityNew2.this) + 1;
        ItemRepository itemdepot = new ItemRepository();
        itemdepot.setId(itemgoid);
        Config.setLastItemId(ClientPageActivityNew2.this, itemgoid);
        itemdepot.setName(getString(R.string.depot_du) + Config.getFormattedDate(Calendar.getInstance().getTime()));
//        itemdepot.setQuantity(1);
        itemdepot.setQuantity(Integer.valueOf(soldes));
//        itemdepot.setUnitPrice(Integer.valueOf(soldes));
//        itemdepot.setTotalPrice(Integer.valueOf(soldes));
        itemdepot = realms.copyToRealmOrUpdate(itemdepot);
        invoicedepot.getItems().add(itemdepot);
        invoicedepot = realms.copyToRealmOrUpdate(invoicedepot);
        depot.setInvoiceRepository(invoicedepot);


        //*********************END TEMP********************//
        // Add the depot to the client
        client.getOperationClients().add(depot);
        int newSolde = client.getSolde() + Integer.valueOf(soldes);
        client.setSolde(newSolde);

        return invoicegoid;
    }

    public void onBackPressed(){

    }
}
