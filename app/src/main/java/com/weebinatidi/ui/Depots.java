package com.weebinatidi.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weebinatidi.Config;
import com.weebinatidi.R;
import com.weebinatidi.model.ArchiveClient;
import com.weebinatidi.model.ClientRepository;
import com.weebinatidi.model.InvoiceRepository;
import com.weebinatidi.model.OperationClientRepository;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.weebinatidi.Config.formaterSolde;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link Depots.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Depots#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Depots extends Fragment {
    public static final String EXTRA_CLIENT_PHONE = "CLIENT_PHONE";
    public static final String EXTRA_CLIENT_ARCHIVE = "CLIENT_ARCHIVE";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    ArchiveClient tmpclient;
    RealmResults<OperationClientRepository> results;
    RealmResults<OperationClientRepository> rembourse;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mDepotList;
    private TextView mClientName;
    private TextView mClientSolde;
    private ImageView mSoldeStateIcon;
    private TextView mClientPhone;
    private TextView mClientEmail;
    private TextView textView;
    private Realm realm;
    private String mPhone;
    private String archived;
    private DepositAdapter mAdapter;
    private ClientRepository thisClient;
    private OperationClientRepository operationClientRepository;
    private ArrayList<String> lesdates = new ArrayList<>();
    private ArrayList<Integer> lesprisescon = new ArrayList<>();
    private ArrayList<Integer> lesidfactures = new ArrayList<>();
    private boolean isInvoice = false;


    private RealmChangeListener reamlListener;
    private Button menu, addcoin, editer, imprimer, prise;

    //private OnFragmentInteractionListener mListener;

    public Depots() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Depots.
     */
    // TODO: Rename and change types and number of parameters
    public static Depots newInstance(String param1, String param2) {
        Depots fragment = new Depots();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhone = getActivity().getIntent().getStringExtra(EXTRA_CLIENT_PHONE);
        archived = getActivity().getIntent().getStringExtra(EXTRA_CLIENT_ARCHIVE);
        realm = Realm.getDefaultInstance();


        // get the actual client info.
        thisClient = realm.where(ClientRepository.class).equalTo("numero", mPhone).findFirst();
        //get operationclient

        RealmList<OperationClientRepository> op = thisClient.getOperationClients();
        results = op.where().equalTo("isInvoice", isInvoice).findAll();
        //rembourse=op.where().notEqualTo("isInvoice", isInvoice).findAll();

        //Toast.makeText(getActivity().getApplicationContext(),""+rembourse,Toast.LENGTH_LONG).show();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_depots, container, false);

        mDepotList = (ListView) view.findViewById(R.id.deposit_liste);
        textView = (TextView) view.findViewById(R.id.solde);
        textView.setText("" + thisClient.getSolde());
        mAdapter = new DepositAdapter(results);
        mDepotList.setAdapter(mAdapter);

        mDepotList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // show the invoice details.



//                Log.d(TAG," size "+mDepotList.getAdapter().getCount());
//                if(position != 0)
//                {
                //because the header has been added the new size is the list size +1 so we need to remove that 1 to make sure
                // we dont get arayboundexception as if not we going out of the array...
//                    Log.d(TAG,"idfacture"+thisClient.getOperationClients().get(0).getInvoiceRepository().getItems().size());
                if ((thisClient.getOperationClients().get(position).getInvoiceRepository() != null) && (thisClient.getOperationClients().get(position).isInvoice() == false)) {

                    InvoiceRepository invoice = thisClient.getOperationClients().get(position).getInvoiceRepository();
                    Intent intent = new Intent(getActivity(), InvoiceDetailsActivity.class);
//                    Log.d(TAG,"idfacture"+invoice.getItems().size());
                    intent.putExtra(InvoiceDetailsActivity.EXTRA_INVOICE_ID, invoice.getId());
                    intent.putExtra(InvoiceDetailsActivity.EXTRA_ISARCHIVE, false);
                    startActivity(intent);
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
        });
        mAdapter.notifyDataSetChanged();
        //Find the +1 button
        //mPlusOneButton = (PlusOneButton) view.findViewById(R.id.plus_one_button);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
        // mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
   /* public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        // mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_depot, parent, false);
            }

            TextView depotType = (TextView) convertView.findViewById(R.id.deposit);
            //TextView depotDate = (TextView) convertView.findViewById(R.id.deposit_date);
            TextView depotMontant = (TextView) convertView.findViewById(R.id.deposit_amount);
            final int lid = mOperationClients.get(position).getInvoiceRepository().getId();
            final InvoiceRepository invoiceRepository = mOperationClients.get(position).getInvoiceRepository();

            int montant = mOperationClients.get(position).getMontant();

            String name = mOperationClients.get(position).getInvoiceRepository().getItems().get(0).getName();
            String lemontant = formaterSolde(montant);
            depotMontant.setText(getString(R.string.montant_egale) + String.valueOf(lemontant));
            depotType.setText(" " + mOperationClients.get(position).getInvoiceRepository().getItems().get(0).getName());
            Config.getFormattedDate(mOperationClients.get(position).getDate());

            //Toast.makeText(getActivity().getApplicationContext(),""+name,Toast.LENGTH_LONG).show();


            /*if(mOperationClients.get(position).getInvoiceRepository() != null)
            {
                if(mOperationClients.get(position).isInvoice() == false ){
                    /*convertView.setBackgroundColor(getResources().getColor(R.color.lightgray));
                    depotType.setTextColor(Color.WHITE);
                    depotMontant.setTextColor(Color.WHITE);
                    depotType.setText(" "+mOperationClients.get(position).getInvoiceRepository().getItems().get(0).getName());
                    String lemontant=formaterSolde(montant);
                    depotMontant.setText(getString(R.string.montant_egale) + String.valueOf(lemontant));
                    depotType.setText(" "+mOperationClients.get(position).getInvoiceRepository().getItems().get(0).getName());
                             Config.getFormattedDate(mOperationClients.get(position).getDate());
                }
            }*/
//
            return convertView;
        }
    }

}