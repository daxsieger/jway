import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IStato } from '../stato.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { StatoService } from '../service/stato.service';
import { StatoDeleteDialogComponent } from '../delete/stato-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-stato',
  templateUrl: './stato.component.html',
})
export class StatoComponent implements OnInit {
  statoes: IStato[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected statoService: StatoService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.statoes = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.statoService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IStato[]>) => {
          this.isLoading = false;
          this.paginateStatoes(res.body, res.headers);
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  reset(): void {
    this.page = 0;
    this.statoes = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IStato): number {
    return item.id!;
  }

  delete(stato: IStato): void {
    const modalRef = this.modalService.open(StatoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.stato = stato;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateStatoes(data: IStato[] | null, headers: HttpHeaders): void {
    const linkHeader = headers.get('link');
    if (linkHeader) {
      this.links = this.parseLinks.parse(linkHeader);
    } else {
      this.links = {
        last: 0,
      };
    }
    if (data) {
      for (const d of data) {
        this.statoes.push(d);
      }
    }
  }
}
