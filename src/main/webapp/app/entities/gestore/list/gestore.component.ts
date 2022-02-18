import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGestore } from '../gestore.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { GestoreService } from '../service/gestore.service';
import { GestoreDeleteDialogComponent } from '../delete/gestore-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-gestore',
  templateUrl: './gestore.component.html',
})
export class GestoreComponent implements OnInit {
  gestores: IGestore[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected gestoreService: GestoreService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.gestores = [];
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

    this.gestoreService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IGestore[]>) => {
          this.isLoading = false;
          this.paginateGestores(res.body, res.headers);
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  reset(): void {
    this.page = 0;
    this.gestores = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IGestore): number {
    return item.id!;
  }

  delete(gestore: IGestore): void {
    const modalRef = this.modalService.open(GestoreDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.gestore = gestore;
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

  protected paginateGestores(data: IGestore[] | null, headers: HttpHeaders): void {
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
        this.gestores.push(d);
      }
    }
  }
}
