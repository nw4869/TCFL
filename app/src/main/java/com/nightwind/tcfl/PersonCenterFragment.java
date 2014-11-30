package com.nightwind.tcfl;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonCenterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonCenterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonCenterFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";

    // TODO: Rename and change types of parameters
    private int mPosition;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Parameter 1.
     * @return A new instance of fragment PersonCenterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonCenterFragment newInstance(int position) {
        PersonCenterFragment fragment = new PersonCenterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public PersonCenterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_POSITION, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_person_center, container, false);

        RelativeLayout menu1 = (RelativeLayout) v.findViewById(R.id.menu1);
        RelativeLayout menu2 = (RelativeLayout) v.findViewById(R.id.menu2);
        RelativeLayout menu3 = (RelativeLayout) v.findViewById(R.id.menu3);
        RelativeLayout menu4 = (RelativeLayout) v.findViewById(R.id.menu4);
        menu1.setOnClickListener(this);
        menu2.setOnClickListener(this);
        menu3.setOnClickListener(this);
        menu4.setOnClickListener(this);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onListPressed(int id) {
        if (mListener != null) {
            mListener.onFragmentInteraction(id);

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        int id;
        switch (v.getId()) {
            case R.id.menu1:
                id = 1;
                break;
            case R.id.menu2:
                id = 2;
                break;
            case R.id.menu3:
                id = 3;
                break;
            case R.id.menu4:
                id = 4;
                break;
            default:
                id = -1;
        }
        onListPressed(id);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(int id);
    }

}
