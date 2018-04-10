import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { Component, ViewChild, OnInit } from '@angular/core';
import { APP_BASE_HREF } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatListModule, MatIconModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';

import { SidebarComponent } from './sidebar.component';
import { IconType } from '../../domain/menu';

describe('SidebarComponent', () => {
  let component: SidebarHostComponent;
  let fixture: ComponentFixture<SidebarHostComponent>;

  beforeEach(
    async(() => {
      TestBed.configureTestingModule({
        declarations: [SidebarComponent, SidebarHostComponent],
        imports: [
          MatListModule,
          MatIconModule,
          FlexLayoutModule,
          HttpClientModule,
          RouterModule.forRoot([])
        ],
        providers: [
          {
            provide: APP_BASE_HREF,
            useValue: '/'
          }
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SidebarHostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create a sidebar', () => {
    expect(component).toBeTruthy();
  });
  it('should create a sidebar with menu data', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h4.mat-line').textContent).toContain(
      '项目一'
    );
  });

  @Component({
    selector: `host-component`,
    template: `<app-sidebar></app-sidebar>`
  })
  class SidebarHostComponent implements OnInit {
    @ViewChild(SidebarComponent) public sidebar: SidebarComponent;
    ngOnInit(): void {
      //Called after the constructor, initializing input properties, and the first call to ngOnChanges.
      //Add 'implements OnInit' to the class.
      this.sidebar.menuGroups = [
        {
          name: '项目列表',
          items: [
            {
              title: '项目一',
              subtitle: '项目一的描述',
              iconName: 'project',
              iconType: IconType.SVG,
              emitData: 'abc123',
              routerLink: ['/projects/abc123']
            },
            {
              title: '项目二',
              subtitle: '项目二的描述',
              iconName: 'home',
              iconType: IconType.MATERIAL_ICON,
              emitData: 'abc123',
              routerLink: ['/projects/abc123']
            },
            {
              title: '项目三',
              subtitle: '项目三的描述',
              iconName: 'fa-bell',
              iconType: IconType.FONT_AWESOME,
              fontSet: 'fontawesome',
              emitData: 'abc123',
              routerLink: ['/projects/abc123']
            }
          ]
        }
      ];
    }
  }
});
