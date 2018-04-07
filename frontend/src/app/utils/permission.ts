import * as _ from 'lodash';

export const showVisible = (
  allowRoleNames: string[],
  userRoleNames: string[]
): boolean => {
  return _.intersection(allowRoleNames, userRoleNames).length > 0;
};
