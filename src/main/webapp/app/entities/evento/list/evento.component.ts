import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IEvento } from '../evento.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EventoService } from '../service/evento.service';
import { EventoDeleteDialogComponent } from '../delete/evento-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-evento',
  templateUrl: './evento.component.html',
})
export class EventoComponent implements OnInit {
  eventos: IEvento[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected eventoService: EventoService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.eventos = [];
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

    this.eventoService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IEvento[]>) => {
          this.isLoading = false;
          this.paginateEventos(res.body, res.headers);
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  reset(): void {
    this.page = 0;
    this.eventos = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IEvento): number {
    return item.id!;
  }

  delete(evento: IEvento): void {
    const modalRef = this.modalService.open(EventoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.evento = evento;
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

  protected paginateEventos(data: IEvento[] | null, headers: HttpHeaders): void {
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
        this.eventos.push(d);
      }
    }
  }
}
