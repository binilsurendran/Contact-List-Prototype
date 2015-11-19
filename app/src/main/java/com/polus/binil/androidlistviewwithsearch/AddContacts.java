package com.polus.binil.androidlistviewwithsearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
public class AddContacts extends Activity {

    EditText contact_name;
    EditText contact_number;
    EditText contact_email;
    ImageView contact_image;
    Bitmap contact_bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contacts);

        contact_name   = (EditText) findViewById(R.id.contact_name);
        contact_number = (EditText) findViewById(R.id.contact_number);
        contact_email  = (EditText) findViewById(R.id.contact_email);

        contact_image  = (ImageView) findViewById(R.id.contact_image);



    }

    public void insertContacts(View v)
    {
        if(contact_name.getText().toString().trim().equals(""))
        {

            contact_name.setError(Html.fromHtml("<font color='red'>Please enter name!</font>"));
        }
        else if(contact_number.getText().toString().trim().equals(""))
        {
            contact_number.setError(Html.fromHtml("<font color='red'>Please enter phone number!</font>"));
        }

        if(!contact_name.getText().toString().trim().equals("") && !contact_number.getText().toString().trim().equals(""))
        {
            if(isValidEmail(contact_email.getText().toString().trim()))
            {
                Log.e("Tag ","valid Contacts");

                ArrayList<ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                contact_name.getText().toString().trim()).build());

                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact_number.getText().toString().trim())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact_email.getText().toString().trim())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if(contact_bitmap!=null){    // If an image is selected successfully
                  //  contact_bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                    contact_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] b = stream.toByteArray();
                    // Adding insert operation to operations list
                    // to insert Photo in the table ContactsContract.Data
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Photo.DATA15,b)
                            .build());

                    try {
                        stream.flush();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Asking the Contact provider to create a new contact
                try {
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    Toast.makeText(AddContacts.this, "Contacts Successfully added", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(AddContacts.this,MainActivity.class);
                    finish();
                    startActivity(i);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AddContacts.this, "Please try again !", Toast.LENGTH_SHORT).show();

                }

            }
            else if(contact_email.getText().toString().trim().equals(""))
            {
                ArrayList<ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                contact_name.getText().toString().trim()).build());

                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact_number.getText().toString().trim())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if(contact_bitmap!=null){    // If an image is selected successfully
                    //  contact_bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                    contact_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] b = stream.toByteArray();
                    // Adding insert operation to operations list
                    // to insert Photo in the table ContactsContract.Data
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Photo.DATA15,b)
                            .build());

                    try {
                        stream.flush();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                // Asking the Contact provider to create a new contact
                try {
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    Toast.makeText(AddContacts.this, "Contacts Successfully added", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(AddContacts.this,MainActivity.class);
                    finish();
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AddContacts.this, "Please try again !", Toast.LENGTH_SHORT).show();

                }
            }
            else
            {
                contact_email.setError(Html.fromHtml("<font color='red'>Please enter valid email address!</font>"));
            }
        }
        else
        {
            Log.e("Tag ","invalid Contacts");
        }
    }

public void addImage(View v)
{
    /*final CharSequence[] options = { "Take Photo", "Choose from Gallery",
            "Cancel" };

    AlertDialog.Builder builder = new AlertDialog.Builder(AddContacts.this);
    builder.setTitle("Add Photo!");
    builder.setItems(options, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            if (options[item].equals("Take Photo")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment
                        .getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, 1);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        }
    });
    builder.show();*/

    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
            /*    File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    contact_bitmap = bitmap;

                    contact_image.setImageBitmap(bitmap);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System
                            .currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                // Getting the uri of the picked photo
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

        Intent i = new Intent(AddContacts.this,MainActivity.class);
        finish();
        startActivity(i);
    }
}
