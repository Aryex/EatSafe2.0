package ca.cmpt276.restauranthealthinspection.ui.filter;

public class MyFilter {
   /* private final MyAdapter myAdapter;

    public MyFilter(MyAdapter myAdapter) {
        this.myAdapter = myAdapter;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint == null || constraint.length() == 0) {
            // unfiltered: show all
            results.values = myAdapter.getOriginalList();
            results.count = myAdapter.getOriginalList().size();
        } else {
            // filtered
            List<Integer> newWorkingList = new ArrayList<>();
            if (constraint.equals('1')) {
                // odd
                for (Integer integer : myAdapter.getOriginalList()) {
                    if (integer % 2 == 1) {
                        newWorkingList.add(integer);
                    }
                }
            } else if (constraint.equals('2')) {
                // even
                for (Integer integer : myAdapter.getOriginalList()) {
                    if (integer % 2 == 0) {
                        newWorkingList.add(integer);
                    }
                }
            }
            results.values = newWorkingList;
            results.count = newWorkingList.size();
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        myAdapter.setFilteredList((List<String>) results.values);
        if (results.count == 0) {
            myAdapter.notifyDataSetInvalidated();
        } else {
            myAdapter.notifyDataSetChanged();
        }
    }*/
}
