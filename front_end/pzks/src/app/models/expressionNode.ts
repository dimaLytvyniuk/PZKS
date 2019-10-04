import { NodeViewModel } from './nodeViewModel';
import { EdgeViewModel } from './edgeViewModel';

export class ExpressionNode {
    level: number;
    nodeType: String;
    value: String;
    leftNode: ExpressionNode;
    rightNode: ExpressionNode;

    transformToViewModel(startId: number, currentLevel: number, nodes: Array<NodeViewModel>, edges: Array<EdgeViewModel>): NodeViewModel {
        let nodeViewModel = new NodeViewModel()
        nodeViewModel.label = this.value;

        if (this.leftNode !== null) {
            let leftNode = Object.assign(new ExpressionNode(), this.leftNode);
            let leftNodeViewModel = leftNode.transformToViewModel(startId, currentLevel + 1, nodes, edges);
            nodes.push(leftNodeViewModel);

            nodeViewModel.leftNode = leftNodeViewModel;
            startId = leftNodeViewModel.id + 1;
        }

        if (this.rightNode !== null) {
            let rightNode = Object.assign(new ExpressionNode(), this.rightNode);
            let rightNodeViewModel = rightNode.transformToViewModel(startId, currentLevel + 1, nodes, edges);
            nodes.push(rightNodeViewModel)

            nodeViewModel.rightNode = rightNodeViewModel;
            startId = rightNodeViewModel.id + 1;
        }

        nodeViewModel.id = startId;
        nodeViewModel.level = currentLevel;

        if (nodeViewModel.leftNode !== undefined) {
            let newEdge = new EdgeViewModel()
            newEdge.from = nodeViewModel.id;
            newEdge.to = nodeViewModel.leftNode.id;
            edges.push(newEdge)
        }

        if (nodeViewModel.rightNode !== undefined) {
            let newEdge = new EdgeViewModel()
            newEdge.from = nodeViewModel.id;
            newEdge.to = nodeViewModel.rightNode.id;
            edges.push(newEdge)
        }

        return nodeViewModel;
    }
}
