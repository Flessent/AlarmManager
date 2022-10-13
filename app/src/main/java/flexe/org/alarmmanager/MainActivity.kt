package flexe.org.alarmmanager

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import flexe.org.alarmmanager.databinding.ActivityMainBinding
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.getSystemService
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding :ActivityMainBinding
private lateinit var timePicker:  MaterialTimePicker
private lateinit var  calendar :Calendar
private  lateinit var alarmManager: AlarmManager
private  lateinit  var pendingIntent : PendingIntent
private  var time :Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
                binding=ActivityMainBinding.inflate(layoutInflater) //or getLayoutInflater()
        setContentView(binding.root)

        createNotificationChannel()

        binding.btnSelectTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                showTimePicker()
            }  })
        binding.btnSetAlarm.setOnClickListener(object :View.OnClickListener{
            override fun onClick(view: View?) {
                setAlarm()

            }
        })

        binding.btnCancelAlarm.setOnClickListener(object :View.OnClickListener{
            override fun onClick(view: View?) {


                cancelAlram()
            }
        })


    }

    private fun cancelAlram() {
        val intent=Intent(this,AlarmReceiver::class.java)

        pendingIntent=PendingIntent.getBroadcast(this,0,intent,0)
        if (alarmManager==null){
            alarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager

        }

        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Alarm cancelled",Toast.LENGTH_SHORT).show()

    }
    private fun setAlarm() {

        alarmManager= getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent=Intent(this,AlarmReceiver::class.java)

        pendingIntent=PendingIntent.getBroadcast(this,0,intent,0)
      //  alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
        //AlarmManager.INTERVAL_DAY,pendingIntent)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            pendingIntent)

        time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
        if (System.currentTimeMillis() > time) {
            // setting time as AM and PM
            if (Calendar.AM_PM== 0)
                time = time + (1000 * 60 * 60 * 12);
            else
                time = time + (1000 * 60 * 60 * 24);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 1000, pendingIntent);
        //AlarmManager.INTERVAL_DAY,pendingIntent
        Toast.makeText(this,"Alarm set successfully", Toast.LENGTH_SHORT).show()


    }

    private fun showTimePicker() {
        this.timePicker=MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm time")
            .build()
        timePicker.show(supportFragmentManager,"foxandroid")
        timePicker.addOnPositiveButtonClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                if (timePicker.hour>12){
                    binding.btnSelectTime.setText(
                        String.format("%02d", (timePicker.hour-12))+":"+String.format("%02d",timePicker.minute)+"PM")


                }else {
                    binding.btnSelectTime.setText( String.format(timePicker.hour.toString() +" :"+timePicker.minute+"AM"))
                }
                calendar= Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY,timePicker.hour)
                calendar.set(Calendar.MINUTE,timePicker.minute)
                calendar.set(Calendar.SECOND,0)
                calendar.set(Calendar.MILLISECOND,0)
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)  {
            val name: CharSequence = "foxandroidReminderChannel"
            val description: String = "Channel for Alarm Mangaer"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel("foxandroid", name, importance)
            notificationChannel.description=description
            val notificationManager:NotificationManager=getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}