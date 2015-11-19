package com.polus.binil.androidlistviewwithsearch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Binil on 11/17/2015.
 */
public class EditContacts extends Activity {

    EditText edt_contact_name;
    EditText edt_contactNumber;
    EditText edt_contactEmail;

    TextView txt_contact_name;
    TextView txt_contactNumber;
    TextView txt_contactEmail;
    TextView txt_email;

    ImageView img_edt;


    String phoneNumber;
    String email="";
    Bitmap photo = null;
    ImageView contact_image;
    String idValue;

    Bitmap contact_bitmap;
    Boolean imageflag = false;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contact);

        edt_contact_name  = (EditText) findViewById(R.id.edt_contact_name);
        edt_contactNumber = (EditText) findViewById(R.id.edt_contactNumber);
        edt_contactEmail  = (EditText) findViewById(R.id.edt_contactEmail);

        txt_contact_name  = (TextView) findViewById(R.id.txt_contact_name);
        txt_contactNumber = (TextView) findViewById(R.id.txt_contactNumber);
        txt_contactEmail  = (TextView) findViewById(R.id.txt_contactEmail);
        txt_email         = (TextView) findViewById(R.id.txt_email);

        contact_image     = (ImageView) findViewById(R.id.contact_image);
        img_edt           = (ImageView) findViewById(R.id.img_edt);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("name");
        txt_contact_name.setText(name);

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                "DISPLAY_NAME = '" + name + "'", null, null);

        if (cursor.moveToFirst()) {
            String contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //
            //  Get all phone numbers.
            //
            final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.STARRED,
                    ContactsContract.Contacts.TIMES_CONTACTED,
                    ContactsContract.Contacts.CONTACT_PRESENCE,
                    ContactsContract.Contacts.PHOTO_ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
            };

            String select = "(" + ContactsContract.Contacts.DISPLAY_NAME + " == \"" + name + "\" )";
            Cursor c = getApplicationContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, CONTACTS_SUMMARY_PROJECTION, select, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
            startManagingCursor(c);

            if (c.moveToNext()) {
                 idValue = c.getString(0);
                ArrayList<String> phones = new ArrayList<String>();
                Boolean flag = false;
                Cursor pCur = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{idValue}, null);
                while (pCur.moveToNext() && flag == false) {
                    flag = true;
                    phones.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY)));
                    Log.i("", name + " has the following phone number " + pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

                    phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    Cursor emailCur = getApplicationContext().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{idValue}, null);
                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));


                    }
                    emailCur.close();

                    Log.e("Tag for edit ","phoneNumber "+ phoneNumber);
                    Log.e("Tag for edit", "Email " + email );

                    try {

                        InputStream inputStream;
                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                             inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getApplicationContext().getContentResolver(),
                                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(idValue)));
                        }
                        else
                        {
                             inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getApplicationContext().getContentResolver(),
                                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(idValue)), true);
                        }

                        if (inputStream != null) {
                            photo = BitmapFactory.decodeStream(inputStream);
                            //  Log.e("Tag", "Photo " + photo);
                            contact_image.setImageBitmap(photo);
                        }
                        else
                        {
                            imageflag = true;
                        }

                        assert inputStream != null;
                        inputStream.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                pCur.close();
                txt_contactNumber.setText(phoneNumber);
                if(!email.equalsIgnoreCase("")) {
                    txt_contactEmail.setText(email);
                }
                else
                {
                    img_edt.setVisibility(View.GONE);
                    txt_email.setVisibility(View.GONE);
                }

            }
        }
        cursor.close();


        edt_contact_name.setText(txt_contact_name.getText().toString());
        edt_contactNumber.setText(txt_contactNumber.getText().toString());
        edt_contactEmail.setText(txt_contactEmail.getText().toString());



    }

    public void editContactName(View v)
    {
        txt_contact_name.setVisibility(View.GONE);
        edt_contact_name.setVisibility(View.VISIBLE);

        edt_contactEmail.clearFocus();
        edt_contactNumber.clearFocus();
        edt_contact_name.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edt_contact_name, InputMethodManager.SHOW_IMPLICIT);

        edt_contact_name.setSelection(edt_contact_name.getText().toString().trim().length());
    }

    public void edtPhoneNumber(View v)
    {
        Log.e("Tag ","edt contact number");
        txt_contactNumber.setVisibility(View.GONE);
        edt_contactNumber.setVisibility(View.VISIBLE);
        edt_contactEmail.clearFocus();
        edt_contact_name.clearFocus();
        edt_contactNumber.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edt_contactNumber, InputMethodManager.SHOW_IMPLICIT);

        edt_contactNumber.setSelection(edt_contactNumber.getText().toString().trim().length());

    }

    public void editEmail(View v)
    {

       Log.e("Tag ","edit email clicked ");
        txt_contactEmail.setVisibility(View.GONE);
        edt_contactEmail.setVisibility(View.VISIBLE);
        edt_contactNumber.clearFocus();
        edt_contact_name.clearFocus();
        edt_contactEmail.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edt_contactEmail, InputMethodManager.SHOW_IMPLICIT);

        edt_contactEmail.setSelection(edt_contactEmail.getText().toString().trim().length());
    }


    public void editContacts(View v)
    {
        if(edt_contact_name.getText().toString().trim().equals(""))
        {

            edt_contact_name.setError(Html.fromHtml("<font color='red'>Please enter name!</font>"));
        }
        else if(edt_contactNumber.getText().toString().trim().equals(""))
        {
            edt_contactNumber.setError(Html.fromHtml("<font color='red'>Please enter phone number!</font>"));
        }

        if(!edt_contact_name.getText().toString().trim().equals("") && !edt_contactNumber.getText().toString().trim().equals(""))
        {



            if(isValidEmail(edt_contactEmail.getText().toString().trim()))
            {
                Log.e("Tag ","imageflag " + imageflag);
                //updateContact(idValue, edt_contact_name.getText().toString().trim(), edt_contactNumber.getText().toString().trim(), edt_contactEmail.getText().toString(), contact_bitmap);
               Log.e("Tag ","valid Contacts");


                ContentResolver contentResolver  = getContentResolver();

                String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

                String[] emailParams = new String[]{idValue, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
                String[] nameParams = new String[]{idValue, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
                String[] numberParams = new String[]{idValue, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
                int photoRow = -1;
                String wherePhoto = ContactsContract.Data.RAW_CONTACT_ID + " = " + idValue + " AND " + ContactsContract.Data.MIMETYPE + " =='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";
                Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, wherePhoto, null, null);
                int idIdx = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID);
                if (cursor.moveToFirst()) {
                    photoRow = cursor.getInt(idIdx);
                }


                ArrayList<android.content.ContentProviderOperation> ops = new ArrayList<android.content.ContentProviderOperation>();

                Log.e("Tag ","Name "+ edt_contact_name.getText().toString().trim());
                Log.e("Tag ","Number "+ edt_contactNumber.getText().toString().trim());
                Log.e("Tag ","Email "+ edt_contactEmail.getText().toString().trim());




                ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                        .withSelection(where,emailParams)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, edt_contactEmail.getText().toString().trim())
                        .build());



                ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                        .withSelection(where,nameParams)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, edt_contact_name.getText().toString().trim())
                        .build());


                ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                        .withSelection(where,numberParams)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, edt_contactNumber.getText().toString().trim())
                        .build());

                ByteArrayOutputStream image = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.PNG, 80, image);




                ByteArrayOutputStream stream = new ByteArrayOutputStream();


                            Log.e("Tag ", "with image ");

                            if (contact_bitmap != null) {    // If an image is selected successfully
                                //  contact_bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                                contact_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                                byte[] bytes = stream.toByteArray();
                                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " +
                                                ContactsContract.Data.MIMETYPE + "=?", new String[]{idValue, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE})
                                        .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                                        .withValue(ContactsContract.Contacts.Photo.PHOTO, bytes)
                                        .build());

                                try {
                                    stream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }





                    //stream.flush();





                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    Toast.makeText(EditContacts.this,"Contact Successfully updated",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(EditContacts.this,MainActivity.class);
                    finish();
                    startActivity(i);

                }
                catch (IllegalArgumentException e)
                {

                }
                catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
            else if(edt_contactEmail.getText().toString().trim().equals(""))
            {
             //   updateContact(idValue, edt_contact_name.getText().toString().trim(), edt_contactNumber.getText().toString().trim(), contact_bitmap);
                ContentResolver contentResolver  = getContentResolver();

                String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

                String[] nameParams = new String[]{idValue, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
                String[] numberParams = new String[]{idValue, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

                ArrayList<android.content.ContentProviderOperation> ops = new ArrayList<android.content.ContentProviderOperation>();

                Log.e("Tag ","Name "+ edt_contact_name.getText().toString().trim());
                Log.e("Tag ","Number "+ edt_contactNumber.getText().toString().trim());

                ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                        .withSelection(where,nameParams)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, edt_contact_name.getText().toString().trim())
                        .build());

                ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                        .withSelection(where,numberParams)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, edt_contactNumber.getText().toString().trim())
                        .build());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if(contact_bitmap!=null){    // If an image is selected successfully
                    //  contact_bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                    contact_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] bytes = stream.toByteArray();

                    ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " +
                                    ContactsContract.Data.MIMETYPE + "=?", new String[]{idValue, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE})
                            .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                            .withValue(ContactsContract.Contacts.Photo.PHOTO, bytes)
                            .build());
                    //stream.flush();

                    try {
                        stream.flush();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                    Toast.makeText(EditContacts.this,"Contact Successfully updated",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(EditContacts.this,MainActivity.class);
                    finish();
                    startActivity(i);

                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }

            }
            else
            {
                edt_contactEmail.setError(Html.fromHtml("<font color='red'>Please enter valid email address!</font>"));
            }
        }
        else
        {
            Log.e("Tag ","invalid Contacts");
        }
    }


    public void deleteContact(View v)
    {
        final ArrayList ops = new ArrayList();
        final ContentResolver cr = getContentResolver();
        ops.add(ContentProviderOperation
                .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?",
                        new String[] { idValue })
                .build());
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Delete This Contact!");

        alertDialog.setMessage("Are you Sure you want to delete this contact?");
        alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {    // DEPRECATED
            public void onClick(DialogInterface dialog, int which) {
                try {
                    cr.applyBatch(ContactsContract.AUTHORITY, ops);
                 //   background_process();
                    ops.clear();
                    Intent i = new Intent(EditContacts.this,MainActivity.class);
                    finish();
                    startActivity(i);
                } catch (OperationApplicationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                } catch (RemoteException e) {
                    // System.out.println(" length :"+i);
                }
                return;
            } });
        alertDialog.setButton2("No", (DialogInterface.OnClickListener)null);    // DEPRECATED
        try {
            alertDialog.show();
        }catch(Exception e) {
            //              Log.e(THIS_FILE, "error while trying to show deletion yes/no dialog");
        }
    }

    public void setImage(View v)
    {
           if(!imageflag) {
               Intent intent = new Intent(Intent.ACTION_PICK);
               intent.setType("image/*");
               startActivityForResult(intent, 1);
           }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImage = data.getData();

                InputStream imageStream = null;
                try {
                    // Getting InputStream of the selected image
                    imageStream = getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // Creating bitmap of the selected image from its inputstream
                contact_bitmap = BitmapFactory.decodeStream(imageStream);

                // Getting reference to ImageView
                //ImageButton ibPhoto = (ImageButton) findViewById(R.id.ib_photo);

                // Setting Bitmap to ImageButton
                contact_image.setImageBitmap(contact_bitmap);
            }
        }
    }







    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(EditContacts.this,MainActivity.class);
        finish();
        startActivity(i);
    }



}
