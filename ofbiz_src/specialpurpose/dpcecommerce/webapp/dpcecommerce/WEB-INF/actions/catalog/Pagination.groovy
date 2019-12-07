import com.olbius.ecommerce.ConfigUtils;


public void processPagination(viewIndex, next, prev, start, end, first, last){
	context.next = next > last ? last : next;
	context.prev = prev < 0 ? 0 : prev;
	context.current = viewIndex;
	context.start = start;
	context.end = end;
	context.first = 0;
	context.last = last;
}
if(context.listSize != null && context.viewSize != null && context.viewIndex != null && context.paginationSize != null){
	total = context.listSize;
	size = context.viewSize;
	index = context.viewIndex;
	paginationSize = context.paginationSize;
	//measure total page
	float div = total / size;

	int totalPage = Math.ceil(div);

	totalPageIndex = totalPage - 1;

	if(viewIndex <= totalPageIndex){
		//measure total size of pagination

		float divPage = totalPage / paginationSize;
		int totalPagination = Math.ceil(divPage);


		int current = index / paginationSize;

		start = current * paginationSize;
		end = start + paginationSize - 1;
		end = end > totalPageIndex ? totalPageIndex : end;

		next = end + 1;
		prev = start - 1;

		processPagination(viewIndex, next, prev, start, end, 0, totalPageIndex);
	}else{
		processPagination(0, 0, 0, -1, 0, 0, 0);
	}
}else{
	processPagination(0, 0, 0, -1, 0, 0, 0);
}
