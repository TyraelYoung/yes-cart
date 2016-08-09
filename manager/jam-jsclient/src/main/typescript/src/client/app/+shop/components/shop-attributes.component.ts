/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import {Component, OnInit, OnDestroy, OnChanges, Input, ViewChild} from '@angular/core';
import {NgIf, NgFor, CORE_DIRECTIVES } from '@angular/common';
import {REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {PaginationComponent} from './../../shared/pagination/index';
import {ShopVO, AttrValueShopVO, AttributeVO, Pair} from './../../shared/model/index';
import {ShopService, ShopEventBus, Util} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {AttributesComponent} from './../../shared/attributes/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';
import {YcValidators} from './../../shared/validation/validators';

@Component({
  selector: 'yc-shop-attributes',
  moduleId: module.id,
  templateUrl: 'shop-attributes.component.html',
  directives: [DataControlComponent, PaginationComponent, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES, ModalComponent, AttributesComponent]
})

export class ShopAttributesComponent implements OnInit, OnChanges {

  @Input() shop:ShopVO;

  shopAttributes:Array<AttrValueShopVO>;
  attributeFilter:string;

  changed:boolean = false;
  validForSave:boolean = false;

  @ViewChild('attributesComponent')
  attributesComponent:AttributesComponent;

  selectedRow:AttrValueShopVO;

  update:Array<Pair<AttrValueShopVO, boolean>>;

  /**
   * Construct shop attribute panel
   *
   * @param _shopService shop service
   */
  constructor(private _shopService:ShopService) {
    console.debug('ShopAttributeComponent constructed');

    this.update = [];

  }

  /** {@inheritDoc} */
  public ngOnInit() {
    console.debug('ShopAttributeComponent ngOnInit shop', this.shop);
    this.onRefreshHandler();
  }

  ngOnChanges(changes:any) {
    console.debug('ShopAttributeComponent ngOnChanges', changes);
    this.onRefreshHandler();
  }

  protected onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.attributesComponent.onRowDeleteSelected();
    }
  }

  protected onRowEditSelected() {
    if (this.selectedRow != null) {
      this.attributesComponent.onRowEditSelected();
    }
  }

  protected onSelectRow(row:AttrValueShopVO) {
    console.debug('ShopAttributeComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  onDataChange(event:any) {
    this.validForSave = event.valid;
    this.update = event.source;
    this.changed = true;
    console.debug('ShopAttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  protected onSaveHandler() {
    console.debug('ShopAttributeComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.update) {

      console.debug('ShopAttributeComponent Save handler update', this.update);

      var _sub:any = this._shopService.saveShopAttributes(this.update).subscribe(
          rez => {
            console.debug('ShopAttributeComponent attributes', rez);
            this.shopAttributes = rez;
            this.changed = false;
            this.selectedRow = null;
            _sub.unsubscribe();
        }
      );
    }
  }

  protected onDiscardEventHandler() {
    console.debug('ShopAttributeComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  protected onRefreshHandler() {
    console.debug('ShopAttributeComponent refresh handler', this.shop);
    this.getShopAttributes();
  }

  /**
   * Read attributes, that belong to shop.
   */
  private getShopAttributes() {
    console.debug('ShopAttributeComponent get attributes', this.shop);
    if (this.shop.shopId > 0) {

      this._shopService.getShopAttributes(this.shop.shopId).subscribe(shopAttributes => {

        console.debug('ShopAttributeComponent attributes', shopAttributes);
        this.shopAttributes = shopAttributes;
        this.changed = false;
        this.selectedRow = null;

      });
    } else {
      this.shopAttributes = null;
      this.selectedRow = null;
    }
  }

}
