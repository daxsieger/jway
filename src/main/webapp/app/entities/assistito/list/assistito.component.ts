import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAssistito } from '../assistito.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { AssistitoService } from '../service/assistito.service';
import { AssistitoDeleteDialogComponent } from '../delete/assistito-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-assistito',
  templateUrl: './assistito.component.html',
})
export class AssistitoComponent implements OnInit {
  assistitos: IAssistito[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected assistitoService: AssistitoService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.assistitos = [];
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

    this.assistitoService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IAssistito[]>) => {
          this.isLoading = false;
          this.paginateAssistitos(res.body, res.headers);
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  reset(): void {
    this.page = 0;
    this.assistitos = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IAssistito): number {
    return item.id!;
  }

  delete(assistito: IAssistito): void {
    const modalRef = this.modalService.open(AssistitoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.assistito = assistito;
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

  protected paginateAssistitos(data: IAssistito[] | null, headers: HttpHeaders): void {
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
        this.assistitos.push(d);
      }
    }
  }
}
