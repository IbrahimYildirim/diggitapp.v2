package com.diggit.android.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.diggit.android.Controller;
import com.diggit.android.ModelFactory;
import com.diggit.android.R;
import com.diggit.android.model.Institution;
import com.diggit.android.model.Person;
import com.diggit.android.model.ProfilePicture;


/**
 * Created by tokb on 30-03-2015.
 */
public class StudentCardActivity extends Activity {
    private static final String TAG = StudentCardActivity.class.getSimpleName();

    private Person loggedPerson;
    private Institution userInstitution;
    private boolean showFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentcard);

        loggedPerson = ModelFactory.getPerson(this);
        userInstitution = ModelFactory.getInstitution(this);
        if (loggedPerson != null) {
            setupCard();
        }

        if (userInstitution != null) {
            setupIfSubscribedInstitution();
        }
        showFront = true;

        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button isValidButton = (Button) findViewById(R.id.isActive);
        isValidButton.setText("Gyldig?");
    }

    private void setupIfSubscribedInstitution() {
        View.OnClickListener turnCardListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnCard();
            }
        };

        Button backToFront = (Button) findViewById(R.id.btn_backToFront); //Button in back
        Button nextPage = (Button) findViewById(R.id.btn_back); //Button in front

        if (userInstitution != null) {

            if (!userInstitution.isSubscribingInstitution()) {
                nextPage.setVisibility(View.INVISIBLE);
            } else {
                nextPage.setOnClickListener(turnCardListener);
                backToFront.setOnClickListener(turnCardListener);
                updateBackside();
            }
        }
    }

    private void updateBackside() {
        TextView lblAddress = (TextView) findViewById(R.id.lbl_inst_address);
        TextView lblPhone = (TextView) findViewById(R.id.lbl_inst_phone);
        TextView lblEmail = (TextView) findViewById(R.id.lbl_inst_email);
        TextView lblCvr = (TextView) findViewById(R.id.lbl_inst_cvr);

        String instAddress = userInstitution.getAdresse() + "\n" +
                userInstitution.getPostnr() + " " + userInstitution.getBynavn() + "\n" +
                userInstitution.getKommune();

        if (userInstitution.getTelefonnr() != null) {
            lblPhone.setText(userInstitution.getTelefonnr().toString());
        }

        if (userInstitution.getAdresse() != null) {
            lblAddress.setText(instAddress);
        }

        if (userInstitution.getMailadresse() != null) {
            lblEmail.setText(userInstitution.getMailadresse());
        }

        if (userInstitution.getCvr() != null) {
            lblCvr.setText(userInstitution.getCvr());
        }

    }

    private void setupButtons() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        //Set icons for button - Font Awesome
        Button btnNextPage = (Button) findViewById(R.id.btn_back);
        btnNextPage.setTypeface(tf);

        Button btnBackToFront = (Button)findViewById(R.id.btn_backToFront);
        btnBackToFront.setTypeface(tf);

        Button btnDeals = (Button) findViewById(R.id.btn_deals);
        btnDeals.setTypeface(tf);
        btnDeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller.showBowntyScreen(StudentCardActivity.this);
            }
        });

        Button isValidButton = (Button) findViewById(R.id.isActive);
        isValidButton.setTypeface(tf);
        isValidButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new IsValidTask().execute();
                    }
                });

        //Hide Deals for now until next update
        btnDeals.setVisibility(View.INVISIBLE);

//        Button reset = (Button)findViewById(R.id.reset);
//        reset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Controller.resetSavedInformation(StudentCardActivity.this);
//            }
//        });
    }

    private void setupCard() {

        TextView lblName = (TextView) findViewById(R.id.lblName);
        TextView lblStudentId = (TextView) findViewById(R.id.lblStudentId);
        TextView lblBirth = (TextView) findViewById(R.id.lblBirthday);
        TextView lblSchool = (TextView) findViewById(R.id.lblSkole);

        if (!loggedPerson.getNavn().isEmpty()) {
            lblName.setText(loggedPerson.getNavn());
        } else {
            lblName.setText("-");
        }

        if (!loggedPerson.getBrugerid().isEmpty()) {
            lblStudentId.setText(loggedPerson.getBrugerid());
        } else {
            lblStudentId.setText("");
        }

        if (!loggedPerson.getFoedselsdag().isEmpty()) {
            lblBirth.setText(loggedPerson.getFoedselsdag());
        } else {
            lblBirth.setText("-");
        }

        if (!loggedPerson.getInstnr().isEmpty()) {
            if (userInstitution != null)
                lblSchool.setText(userInstitution.getNavn());
        } else {
            lblSchool.setText("-");
        }

        ProfilePicture profilePicture = ModelFactory.getProfilePicture(getApplicationContext());
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(profilePicture.getImage());
    }

    private void turnCard() {
        ViewGroup studentCardFront = (ViewGroup) findViewById(R.id.card_front);
        ViewGroup studentCardBack = (ViewGroup)findViewById(R.id.card_back);

        if (showFront) {
            Controller.fadeViewOut(studentCardFront, 0);
            Controller.fadeViewin(studentCardBack, 300);
        } else {
            //Turn to front
            Controller.fadeViewOut(studentCardBack, 0);
            Controller.fadeViewin(studentCardFront, 300);
        }
        showFront = !showFront;
    }

    private class IsValidTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return ModelFactory.isValid(StudentCardActivity.this);
        }

        @Override
        protected void onPostExecute(Boolean isValid) {
            super.onPostExecute(isValid);

            Button isValidButton = (Button) findViewById(R.id.isActive);
            isValidButton.setTextColor(isValid ? Color.GREEN : Color.RED);
            isValidButton.setText(isValid ? "\uf164" : Html.fromHtml("\uf145")); //Tnumb-up or down
        }
    }
}
