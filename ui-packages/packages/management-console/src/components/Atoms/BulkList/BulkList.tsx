import React from 'react';
import {
  TextContent,
  TextVariants,
  Text,
  Divider,
  TextList,
  TextListItem
} from '@patternfly/react-core';
import {
  ItemDescriptor,
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/common';

export enum OperationType {
  ABORT = 'ABORT',
  SKIP = 'SKIP',
  RETRY = 'RETRY',
  CANCEL = 'CANCEL'
}

export enum BulkListType {
  PROCESS_INSTANCE = 'process_instance',
  JOB = 'job'
}
export interface IOperationResult {
  successItems: BulkListItem[];
  failedItems: BulkListItem[];
  ignoredItems: BulkListItem[];
}
export interface IOperationMessages {
  successMessage: string;
  warningMessage?: string;
  ignoredMessage: string;
  noItemsMessage: string;
}

export interface IOperationFunctions {
  perform: () => void;
}

export interface IOperationResults {
  [key: string]: IOperationResult;
}

export interface IOperation {
  type: string;
  results: IOperationResult;
  messages: IOperationMessages;
  functions: IOperationFunctions;
}

export interface IOperations {
  [key: string]: IOperation;
}
export interface IOwnProps {
  operationResult: IOperation;
}
export interface BulkListItem {
  id: string;
  name: string;
  description: string;
  errorMessage?: string;
}

const BulkList: React.FC<IOwnProps & OUIAProps> = ({
  operationResult,
  ouiaId,
  ouiaSafe
}) => {
  const iterateItems = (itemList: BulkListItem[]) => {
    return (
      <TextList>
        {itemList.map((item: BulkListItem) => {
          return (
            <TextListItem key={item.id}>
              <strong>
                <ItemDescriptor itemDescription={item} />
              </strong>{' '}
              {item.errorMessage && <span> - {item.errorMessage}</span>}
            </TextListItem>
          );
        })}
      </TextList>
    );
  };
  return (
    <div {...componentOuiaProps(ouiaId, 'bulk-list', ouiaSafe)}>
      {operationResult.results.successItems.length > 0 ? (
        <>
          <TextContent>
            <Text component={TextVariants.h2}>
              {operationResult.messages.successMessage}
            </Text>
            {iterateItems(operationResult.results.successItems)}
          </TextContent>
          {operationResult.results.successItems.length !== 0 &&
            operationResult.messages.warningMessage && (
              <TextContent className="pf-u-mt-sm">
                <Text component={TextVariants.small}>
                  {operationResult.messages.warningMessage}
                </Text>
              </TextContent>
            )}
        </>
      ) : (
        <TextContent>
          <Text component={TextVariants.h2}>
            {operationResult.messages.noItemsMessage}
          </Text>
        </TextContent>
      )}
      {operationResult.results.ignoredItems.length !== 0 && (
        <>
          <Divider component="div" className="pf-u-my-xl" />
          <TextContent>
            <Text component={TextVariants.h2}>
              {operationResult.type === BulkListType.PROCESS_INSTANCE && (
                <span>Ignored process instances:</span>
              )}
              {operationResult.type === BulkListType.JOB && (
                <span>Ignored jobs:</span>
              )}
            </Text>
            <Text component={TextVariants.small} className="pf-u-mt-sm">
              <span>{operationResult.messages.ignoredMessage}</span>
            </Text>
            {iterateItems(operationResult.results.ignoredItems)}
          </TextContent>
        </>
      )}
      {operationResult.results.failedItems.length !== 0 && (
        <>
          <Divider component="div" className="pf-u-my-xl" />
          <TextContent>
            <Text component={TextVariants.h2}>Errors:</Text>
            {iterateItems(operationResult.results.failedItems)}
          </TextContent>
        </>
      )}
    </div>
  );
};

export default BulkList;
