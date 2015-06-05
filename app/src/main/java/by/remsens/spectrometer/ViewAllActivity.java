package by.remsens.spectrometer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class ViewAllActivity extends Activity implements FilenameFilter, OnMenuItemClickListener, OnItemClickListener {
	String FileNameList[];
	 GridView gridView;	 	
	   MyAdapter myAdapter;
	   final int REQUEST_CODE_DELETE= 1;
	   
	   
	      class Item
	        {
	            final String name;
	            final Bitmap drawableId;
	            final String FileName;
	            boolean isChecked;


	            Item(String name,Bitmap drawableId, String FileName)
	            {
	                this.name = name;
	                this.drawableId = drawableId;
	                this.FileName = FileName;
	                isChecked=false;

	            }
	        }
	      
	 @Override
	   public boolean accept(File dir, String name) {
	      return name.endsWith(".jpg");
	   }
	 
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {	    
		    super.onCreateOptionsMenu(menu);
		/*    menu.add("Оправить")
	        
	        .setOnMenuItemClickListener(this)	  
	        .setIcon(R.drawable.send_envelope_diskette)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);*/

		
		    menu.add("Удалить")
		    
		    .setIcon(R.drawable.trash)
	        
	        .setOnMenuItemClickListener(this)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM );
		    
		    menu.add("Выделить все")
	        .setIcon(R.drawable.checkmark_checkmark)
	        .setOnMenuItemClickListener(this)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		    menu.add("Отменить выделение")
	        .setIcon(R.drawable.checkmark_close)
	        .setOnMenuItemClickListener(this)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		    

		    menu.add("Назад")
	        .setIcon(R.drawable.arrow_left)
	        .setOnMenuItemClickListener(this)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		    
	    
		    return true;
		    
		}
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState)
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_view_all);
	        this.setTitle("Просмотр  отснятого материала");
	    }
	 
	 @Override
	 public void onResume(){
		 super.onResume();
	        
	        
	      
	        this.getActionBar().setDisplayShowTitleEnabled(true);
	        
	        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/SpecApp/";	   
	    
	        File f = new File(path);        
	        FileNameList = f.list(this);
	     
	        for (int i=0; i < FileNameList.length; i++)
	        {
	        	FileNameList[i] = path +FileNameList[i];
	        	        }
	        
	        if (FileNameList.length==0) {
	        	//final Activity activitity =this;
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setTitle("");
	        	builder.setMessage("Нет отснятых кадров");
	        	builder.setCancelable(false);
	        	builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
	        	    @Override
	        	    public void onClick(DialogInterface dialog, int which) {
	        	    	finish();
	        	        //dialog.dismiss(); // Отпускает диалоговое окно		
	        	        
	        	    }
	        	});
	        	AlertDialog dialog = builder.create();
	        	dialog.show();
	        }
	        
	        gridView = (GridView)findViewById(R.id.gridview);
	        
	        
	        gridView.setOnItemClickListener(this);
	        
	        
	         myAdapter  =new MyAdapter(this,FileNameList); 
	        gridView.setAdapter(myAdapter);
	   
	        
	   
	       


	    
	    }
	 



	    private class MyAdapter extends BaseAdapter
	    {
	        private List<Item> items = new ArrayList<Item>();

	        private LayoutInflater inflater;

	        
	        
	        
	        public MyAdapter(Context context, String[] FileNameList)
	        {
	            inflater = LayoutInflater.from(context);

	            for (int i = 0; i < FileNameList.length; i++) {
	            	File file = new File(FileNameList[i]);
	            	Date lastModDate = new Date(file.lastModified());
	            	
	            	items.add(new Item(lastModDate.toString(), Bitmap.createScaledBitmap(BitmapFactory.decodeFile(FileNameList[i]),240,240,false),FileNameList[i]));
	            	
	            }

	            	       
	        }

	        public void deleteItem(int index) {
	        	  items.remove(index);
	        }
	        
	        @Override
	        public int getCount() {
	            return items.size();
	        }

	        @Override
	        public Object getItem(int i)
	        {
	            return items.get(i);
	        }

	
	        


	        @Override
	        public View getView(int i, View view, ViewGroup viewGroup)
	        {
	            View v = view;
	            ImageView picture;
	            TextView name;
	            CheckBox box;

	            if(v == null)
	            {
	               v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
	               v.setTag(R.id.picture, v.findViewById(R.id.picture));
	               v.setTag(R.id.text, v.findViewById(R.id.text));
	               v.setTag(R.id.checkBox,v.findViewById(R.id.checkBox));
	            }

	            picture = (ImageView)v.getTag(R.id.picture);
	            name = (TextView)v.getTag(R.id.text);
	            box = (CheckBox)v.getTag(R.id.checkBox);
	            
	           

	            final Item item = (Item)getItem(i);
	            box.setChecked(item.isChecked); 
	            box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isChecked) {
						item.isChecked = isChecked;
						
					}
	            	
	            });
	            
	            picture.setImageBitmap(item.drawableId);
	            
	 
	            name.setText(item.name);

	            return v;
	        }


	        
	  

	   		@Override
	   		public long getItemId(int i) {
	   		
	   			return i;
	   		}
	    }


		@Override
		public boolean onMenuItemClick(MenuItem item) {
			if (item.getTitle().toString().contains("Назад")) this.finish(); 
	

	
            for (int i = 0; i < myAdapter.getCount(); i++) {
            	Item GridViewItem = (Item)myAdapter.getItem(i);
            	if (item.getTitle().toString().contains("Выделить все"))  GridViewItem.isChecked=true;
            	else if  (item.getTitle().toString().contains("Отменить выделение")) GridViewItem.isChecked=false;
            	else if ((item.getTitle().toString().contains("Удалить")) && (GridViewItem.isChecked)) {
            		String PhotoFileName = ((Item)myAdapter.getItem(i)).FileName;
    	    		String SpectrumFileName = FileNameList[i].replace(".jpg", ".spe").replace("IMG_","SPE_");
    	    		myAdapter.deleteItem(i);	    		
    	    		myAdapter.notifyDataSetChanged();			    	
    	    		new File(PhotoFileName).delete();
    	    		new File(SpectrumFileName).delete();
            	}
            }

            myAdapter.notifyDataSetChanged();
		
		/*	for (int i = 0; i < deleteList.size(); i++) {
				String PhotoFileName = ((Item)myAdapter.getItem(i)).FileName;
	    		String SpectrumFileName = FileNameList[i].replace(".jpg", ".spe").replace("IMG_","SPE_");
	    		myAdapter.deleteItem(i);	    		
	    		myAdapter.notifyDataSetChanged();			    	
	    		new File(PhotoFileName).delete();
	    		new File(SpectrumFileName).delete();
			}*/
			return false;
		}
		
		
		  @Override
		  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    // запишем в лог значения requestCode и resultCode
		    Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
		    myAdapter.notifyDataSetChanged();		
		    // если пришло ОК
		    if (resultCode == RESULT_OK) {
		        switch (requestCode) {
		        case REQUEST_CODE_DELETE:
		          myAdapter.notifyDataSetChanged();		
		          break;
		        }
		    }
		  }

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
			
			String PhotoFileName = ((Item)parent.getItemAtPosition(position)).FileName;
			
			String SpectrumFileName = PhotoFileName.replace(".jpg", ".spe").replace("IMG_","SPE_");
	
			
			  Intent intent = new Intent(this, ViewOneActivity.class);
			  Bundle b = new Bundle();
			  b.putString("PhotoFileName", PhotoFileName); 
			  b.putString("SpectrumFileName", SpectrumFileName);
			//  b.putIntArray("SpectrumArray", SpectrumArray);
			  intent.putExtras(b); //Put your id to your next Intent
			  startActivityForResult(intent, REQUEST_CODE_DELETE);

			
		}
}
