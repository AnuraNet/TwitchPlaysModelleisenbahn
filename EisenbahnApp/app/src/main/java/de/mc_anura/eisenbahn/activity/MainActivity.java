package de.mc_anura.eisenbahn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Objects;

import de.mc_anura.eisenbahn.DataContainer;
import de.mc_anura.eisenbahn.R;
import de.mc_anura.eisenbahn.task.LevelTask;
import de.mc_anura.eisenbahn.task.StatesTask;
import de.mc_anura.eisenbahn.task.ToggleTask;

public class MainActivity extends AppCompatActivity {

    private final BiMap<String, Integer> ids = HashBiMap.create();
    private int viewId = 0;
    private LinearLayout buttons;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            String id = ids.inverse().get(((View) seekBar.getParent()).getId());
            Log.d("C" + id, String.valueOf(seekBar.getProgress()));
            new LevelTask((status) -> {
                if (status.startsWith("error:")) {
                    Snackbar.make(buttons, "Error while changing level: " + status.replace("error:", ""), Snackbar.LENGTH_LONG)
                            .show();
                    updateStates();
                }
            }).execute(new DataContainer(id, seekBar.getProgress(), DataContainer.DataType.INT));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> updateStates());

        buttons = findViewById(R.id.buttons);
        updateStates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateStates() {
        new StatesTask((map) -> {
            if (map.isEmpty()) {
                Snackbar.make(buttons, "Error while refreshing states", Snackbar.LENGTH_LONG)
                        .show();
            }

            map.forEach((key, value) -> {
                boolean found = false;
                for (int i = 0; i < buttons.getChildCount(); i++) {
                    View v = buttons.getChildAt(i);
                    if (Objects.equals(v.getId(), ids.get(key))) {
                        found = true;
                        switch (value.getDataType()) {
                            case BOOLEAN:
                                Switch s = (Switch) v;
                                s.setOnCheckedChangeListener(null);
                                s.setChecked(value.getValue());
                                s.setOnCheckedChangeListener(this::toggleSwitch);
                                break;
                            case INT:
                                LinearLayout ll = (LinearLayout) v;
                                SeekBar sb = (SeekBar) ll.getChildAt(1);
                                sb.setProgress(value.getValue());
                                break;
                        }
                    }
                }
                if (!found) {
                    switch (value.getDataType()) {
                        case BOOLEAN:
                            createSwitch(key, value.getValue());
                            break;
                        case INT:
                            createSeekBar(key, value.getValue());
                            break;
                        default:
                            break;
                    }
                }
            });
        }).execute();
    }

    private void createSeekBar(String key, Integer value) {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setId(viewId++);
        TextView tv = new TextView(this);
        tv.setText(key);
        tv.setTextColor(getResources().getColorStateList(android.R.color.black, this.getTheme()));
        tv.setPadding(10, 25, 10, 25);
        tv.setTextSize(24f);
        ll.addView(tv, 0);
        SeekBar sb = new SeekBar(this);
        sb.setMax(100);
        sb.setProgress(value);
        sb.setOnSeekBarChangeListener(seekBarChangeListener);
        sb.setPadding(50, 25, 50, 25);
        ll.addView(sb, 1);
        ids.put(key, ll.getId());
        buttons.addView(ll);
    }

    private void createSwitch(String key, Boolean value) {
        Switch s = new Switch(this);
        s.setText(key);
        s.setChecked(value);
        s.setOnCheckedChangeListener(this::toggleSwitch);
        s.setPadding(10, 25, 10, 25);
        s.setTextSize(24f);
        s.setId(viewId++);
        ids.put(key, s.getId());
        buttons.addView(s);
    }

    private void toggleSwitch(CompoundButton button, boolean isChecked) {
        String id = ids.inverse().get(button.getId());
        new ToggleTask((status) -> {
            if (status.startsWith("error:")) {
                Snackbar.make(buttons, "Error while toggling states: " + status.replace("error:", ""), Snackbar.LENGTH_LONG)
                        .show();
                updateStates();
            } else {
                button.setOnCheckedChangeListener(null);
                button.setChecked(Boolean.valueOf(status));
                button.setOnCheckedChangeListener(this::toggleSwitch);
            }
        }).execute(id);
    }
}
