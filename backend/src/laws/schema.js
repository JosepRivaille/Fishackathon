import { makeExecutableSchema } from 'graphql-tools';

import resolvers from './resolvers';

const typeDefs = `
    type Law {
        id: String
        title: String
        abstract: String
        resource: String
        affects: [Classification]
        time: TimeWindow
        affectsIn: Int
    }
    
    type TimeWindow {
        start: String
        end: String
    }
    
    input TimeWindow2 {
        start: String
        end: String
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
            timeWindow: TimeWindow2
            affectsIn: Int
        ): Law
    }
    
    enum Classification {
        PROFESSIONAL
        RECREATIONAL
        DRAGGER
        FENCE
        NET
        FLY
        TRAP
        CORAL
        HOOK
    }
`;

const schema = makeExecutableSchema({typeDefs, resolvers});
export default schema;
