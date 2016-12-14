package com.savvasdalkitsis.gameframe.draw.view;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.savvasdalkitsis.butterknifeaspects.aspects.BindLayout;
import com.savvasdalkitsis.gameframe.R;
import com.savvasdalkitsis.gameframe.draw.model.DrawingMode;
import com.savvasdalkitsis.gameframe.draw.presenter.DrawPresenter;
import com.savvasdalkitsis.gameframe.grid.model.ColorGrid;
import com.savvasdalkitsis.gameframe.grid.view.GridTouchedListener;
import com.savvasdalkitsis.gameframe.grid.view.LedGridView;
import com.savvasdalkitsis.gameframe.infra.view.FragmentSelectedListener;
import com.savvasdalkitsis.gameframe.infra.view.Snackbars;
import com.shazam.android.aspects.base.fragment.AspectSupportFragment;

import butterknife.Bind;
import rx.functions.Action1;

import static com.savvasdalkitsis.gameframe.draw.model.Palette.Builder.palette;
import static com.savvasdalkitsis.gameframe.injector.presenter.PresenterInjector.drawPresenter;

@BindLayout(R.layout.fragment_draw)
public class DrawFragment extends AspectSupportFragment implements FragmentSelectedListener,
        SwatchSelectedListener, GridTouchedListener, DrawView, ColorChooserDialog.ColorCallback {

    @Bind(R.id.view_draw_led_grid_view)
    public LedGridView ledGridView;
    @Bind(R.id.view_draw_palette)
    public PaletteView paletteView;
    FloatingActionButton fab;
    @Bind(R.id.view_draw_pencil)
    View pencil;
    @Bind(R.id.view_draw_fill)
    View fill;
    private DrawingMode drawingMode;
    private int color;
    private View fabProgress;
    private final DrawPresenter presenter = drawPresenter();
    @Nullable
    private SwatchView swatchToModify;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.bindView(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.view_fab);
        fabProgress = getActivity().findViewById(R.id.view_fab_progress);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ledGridView.setOnGridTouchedListener(this);
        paletteView.bind(palette()
                .colors(getResources().getIntArray(R.array.palette))
                .build());
        paletteView.setOnSwatchSelectedListener(this);
        fill.setOnClickListener(v -> {
            fill.setAlpha(1);
            pencil.setAlpha(0.2f);
            drawingMode = DrawingMode.FILL;
        });
        pencil.setOnClickListener(v -> {
            pencil.setAlpha(1);
            fill.setAlpha(0.2f);
            drawingMode = DrawingMode.PENCIL;
        });
        pencil.performClick();
    }

    @Override
    public void onFragmentSelected() {
        fab.setOnClickListener(v -> presenter.upload(ledGridView.getColorGrid()));
        fab.setImageResource(R.drawable.ic_publish_white_48px);
    }

    @Override
    public void onSwatchSelected(int color) {
        this.color = color;
    }

    @Override
    public void onSwatchLongPressed(SwatchView swatch) {
        swatchToModify = swatch;
        new ColorChooserDialog.Builder((AppCompatActivity & ColorChooserDialog.ColorCallback) getContext(), R.string.change_color)
                .allowUserColorInputAlpha(false)
                .show();
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        if (swatchToModify != null) {
            swatchToModify.bind(selectedColor);
        }
    }

    @Override
    public void onGridTouchedListener(int column, int row) {
        ColorGrid colorGrid = ledGridView.getColorGrid();
        ledGridView.display(colorGrid);
        switch (drawingMode) {
            case FILL:
                colorGrid.fill(color);
                break;
            case PENCIL:
                colorGrid.setColor(color, column, row);
                break;
        }
        ledGridView.invalidate();
    }

    @Override
    public void askForFileName(Action1<String> nameSelected) {
        new MaterialDialog.Builder(getActivity())
                .input(R.string.name_of_drawing, 0, false, (dialog, input) -> nameSelected.call(input.toString()))
                .title(R.string.enter_name_for_drawing)
                .positiveText(R.string.upload)
                .negativeText(android.R.string.cancel)
                .build()
                .show();
    }

    @Override
    public void fileUploaded() {
        Snackbars.success(coordinator(), R.string.success).show();
        scaleProgress(0);
    }

    @Override
    public void failedToUpload(Throwable e) {
        Log.e(DrawPresenter.class.getName(), "Error uploading to game frame", e);
        Snackbars.error(coordinator(), R.string.operation_failed).show();
        scaleProgress(0);
    }

    @Override
    public void displayUploading() {
        scaleProgress(1);
    }

    @Override
    public void drawingAlreadyExists(String name, ColorGrid colorGrid, Throwable e) {
        Log.e(DrawPresenter.class.getName(), "Drawing already exists", e);
        Snackbars.actionError(coordinator(), R.string.already_exists, R.string.replace,
                view -> presenter.replaceDrawing(name, colorGrid)).show();
        scaleProgress(0);
    }

    @Override
    public void failedToDelete(Throwable e) {
        Log.e(DrawPresenter.class.getName(), "Failed to delete drawing", e);
        Snackbars.error(coordinator(), R.string.operation_failed).show();
    }

    private void scaleProgress(int value) {
        fabProgress.animate().scaleX(value).scaleY(value).start();
    }

    private View coordinator() {
        return getActivity().findViewById(R.id.view_coordinator);
    }
}
