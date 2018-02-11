import { makeExecutableSchema } from 'graphql-tools';

import resolvers from './resolvers';

const typeDefs = `
    type Law {
        id: String
        title: String
        abstract: String
        resource: String
        affects: [Classification]
    }

    type Query {
        laws(affects: [Classification]): [Law]
        law(id: Int!): Law
    }
    
    type Mutation {
        addLaw(
            title: String!,
            abstract: String,
            resource: String!,
            affects: [Classification],
            bans: [Classification]
        ): Law
    }
    
    enum Classification {
        PROFESSIONAL
        RECREATIONAL
        DRAGGER
    }
`;

const schema = makeExecutableSchema({typeDefs, resolvers});
export default schema;
