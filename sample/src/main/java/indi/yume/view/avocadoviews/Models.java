package indi.yume.view.avocadoviews;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;

/**
 * Created by yume on 17-4-20.
 */
@UtilityClass
public class Models {

    ItemModel generaModel(int index) {
        return new ItemModel(
                index,
                "Title " + index,
                "This is Content " + index
        );
    }

    List<ItemModel> genList(int startIndex, int count) {
        List<ItemModel> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(generaModel(startIndex + i));
        }

        return list;
    }
}
