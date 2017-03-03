package uci.horarioUCI;

import java.util.List;

import uci.horarioUCI.UpdateService.ServiceHorarioUpdate;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HorarioUCIUpdate extends Activity {

	private Button btnService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_horario_uciupdate);

		btnService = (Button) findViewById(R.id.btnService);

		if (enEjecucion()) {
			btnService.setText("Parar servicio");
		}
		btnService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (enEjecucion()) {
					stopService(new Intent(HorarioUCIUpdate.this,
							ServiceHorarioUpdate.class));
					btnService.setText("Iniciar servicio");
				} else {
					startService(new Intent(HorarioUCIUpdate.this,
							ServiceHorarioUpdate.class));
					btnService.setText("Parar servicio");
				}

			}
		});
	}

	/**
	 * Determina si el proceso de actualizacion del horario esta ejecutandose
	 * 
	 * @return true si se ejecuta, false en otro caso
	 */
	private boolean enEjecucion() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = manager
				.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo service : runningServices) {
			if (ServiceHorarioUpdate.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
