import { Injectable } from '@angular/core';
import { ExpressionTree } from '../models/expressionTree';
import { TreeViewModel } from '../models/treeViewModel';
import { NodeViewModel } from '../models/nodeViewModel';
import { EdgeViewModel } from '../models/edgeViewModel';
import { ExpressionNode } from '../models/expressionNode';

@Injectable({
  providedIn: 'root'
})
export class TreeBuilderService {

  constructor() { }

  buildTreeViewModel(expressionTree: ExpressionTree): TreeViewModel {
    let nodes = new Array<NodeViewModel>()
    let edges = new Array<EdgeViewModel>()

    let head = Object.assign(new ExpressionNode(), expressionTree.head)
    let headViewModel = head.transformToViewModel(1, 0, nodes, edges);

    nodes.push(headViewModel);

    let treeViewModel = new TreeViewModel();
    treeViewModel.edges = edges;
    treeViewModel.nodes = nodes;

    return treeViewModel;
  }
}
