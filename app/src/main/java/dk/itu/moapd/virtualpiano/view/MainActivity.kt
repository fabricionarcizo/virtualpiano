package dk.itu.moapd.virtualpiano.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dk.itu.moapd.virtualpiano.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment)

        if (fragment == null)
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.fragment,
                    MainFragment()
                )
                .commit()
    }

}
