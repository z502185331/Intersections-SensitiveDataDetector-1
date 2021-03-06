package capstone.sdd.core;

import capstone.sdd.gui.GuiListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;


public class ScanWorker implements Callable<Void> {
	
	private File folder;
	private Settings settings;
	private ExecutorService pool;
	private GuiListener listener;
	
	public ScanWorker(File folder, ExecutorService pool, GuiListener listener) {

		if (folder == null || pool == null || listener == null) {
			return;
		}

		this.folder = folder;
		this.pool = pool;
		settings = Settings.getInstance();

		this.listener = listener;
	}

	@Override
	public Void call() throws Exception {
		
		File[] files = folder.listFiles();

		for (File file : files) {
			if (file == null) {
				continue;
			}
				
			if (file.isDirectory()) {
				pool.submit(new ScanWorker(file, pool, listener));
			} else{
				if (settings.isSupported(file) && file.length() <= settings.getFileSizeLimit()) {
					listener.addTask(file);
				}
			}
		}

		
		return null;
	}

}
