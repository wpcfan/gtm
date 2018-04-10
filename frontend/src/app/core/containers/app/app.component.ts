import { Component } from '@angular/core';
import { MenuGroup, IconType } from '../../../domain/menu';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  isCollapsed = true;
  sidebarMenu: MenuGroup[] = [];
  constructor() {
    this.sidebarMenu = [
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
